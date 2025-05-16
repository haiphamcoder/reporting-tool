package com.haiphamcoder.reporting.application.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.haiphamcoder.reporting.application.service.EmitterService;
import com.haiphamcoder.reporting.domain.model.SseEvent;
import com.haiphamcoder.reporting.domain.repository.InMemoryEmitterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmitterServiceImpl implements EmitterService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final InMemoryEmitterRepository inMemoryEmitterRepository;
    private final long heartbeatInterval;
    private final long eventTimeout;
    private final AtomicBoolean logNotFoundActiveEmitter = new AtomicBoolean(true);

    public EmitterServiceImpl(InMemoryEmitterRepository inMemoryEmitterRepository,
            @Value("${sse.heartbeat.interval:30000}") long heartbeatInterval,
            @Value("${sse.event.timeout:60000}") long eventTimeout) {
        this.inMemoryEmitterRepository = inMemoryEmitterRepository;
        this.heartbeatInterval = heartbeatInterval;
        this.eventTimeout = eventTimeout;
        startHeartbeatScheduler();
    }

    @Override
    public SseEmitter createEmitter(String id) {
        SseEmitter emitter = new SseEmitter(eventTimeout);

        emitter.onCompletion(() -> {
            log.info("Emitter for id {} completed", id);
            inMemoryEmitterRepository.removeEmitter(id, emitter);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter for id {} timed out", id);
            inMemoryEmitterRepository.removeEmitter(id, emitter);
        });

        emitter.onError(ex -> {
            log.error("Emitter for id {} error. {}", id, ex.getMessage());
            inMemoryEmitterRepository.removeEmitter(id, emitter);
        });

        inMemoryEmitterRepository.addEmitter(id, emitter);
        return emitter;
    }

    private void startHeartbeatScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            Set<Entry<String, List<SseEmitter>>> entries = inMemoryEmitterRepository.getEmitterMap().entrySet();
            if (entries.isEmpty()) {
                if (logNotFoundActiveEmitter.compareAndSet(true, false)) {
                    log.info("No active emitters found");
                }
                return;
            } else {
                logNotFoundActiveEmitter.set(true);
                log.info("Sending heartbeat to all active emitters");
                for (Entry<String, List<SseEmitter>> entry : entries) {
                    String id = entry.getKey();
                    for (SseEmitter emitter : entry.getValue()) {
                        try {
                            emitter.send(SseEmitter.event().name(SseEvent.Type.HEARTBEAT.getValue()).data("ping..."));
                        } catch (IOException e) {
                            log.warn("Removing closed emitter for id {}", id);
                            inMemoryEmitterRepository.removeEmitter(id, emitter);
                        }
                    }
                }
            }
        }, 5000, heartbeatInterval, TimeUnit.MILLISECONDS);
    }
}
