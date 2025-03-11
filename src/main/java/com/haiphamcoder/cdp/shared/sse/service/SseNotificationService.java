package com.haiphamcoder.cdp.shared.sse.service;

import java.util.List;
import java.util.concurrent.CompletionException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.haiphamcoder.cdp.shared.StringUtils;
import com.haiphamcoder.cdp.shared.sse.model.SseEvent;
import com.haiphamcoder.cdp.shared.sse.repository.InMemoryEmitterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseNotificationService {
    private final InMemoryEmitterRepository inMemoryEmitterRepository;

    public SseNotificationService(InMemoryEmitterRepository inMemoryEmitterRepository) {
        this.inMemoryEmitterRepository = inMemoryEmitterRepository;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down, completing all emitters");
            inMemoryEmitterRepository.getEmitterMap().values().forEach(emitters -> emitters.forEach(SseEmitter::complete));
        }));
    }

    public void sendNotification(String id, SseEvent.Type type, Object data) {
        if (StringUtils.isNullOrEmpty(id)) {
            log.warn("Id is null or empty, skipping notification");
            return;
        }

        if (type == null) {
            log.warn("Type is null, skipping notification");
            return;
        }

        List<SseEmitter> emitters = inMemoryEmitterRepository.getEmitters(id);
        if (emitters == null || emitters.isEmpty()) {
            log.warn("No emitters found for id {}, skipping notification", id);
            return;
        }
        emitters.forEach(emitter -> {
            try {
                log.info("Sending notification to emitter for id {}", id);
                emitter.send(SseEmitter.event().name(type.getValue()).data(data));
            } catch (Exception e) {
                try {
                    log.error("Error sending notification to emitter for id {}", id, e);
                    emitter.completeWithError(e);
                } catch (CompletionException ex) {
                    log.error("Error completing emitter for id {}", id, ex);
                } finally {
                    inMemoryEmitterRepository.removeEmitter(id, emitter);
                }
            }
        });
    }
}
