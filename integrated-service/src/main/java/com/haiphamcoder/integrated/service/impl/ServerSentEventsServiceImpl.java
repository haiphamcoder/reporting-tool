package com.haiphamcoder.integrated.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.haiphamcoder.integrated.config.CommonConstants;
import com.haiphamcoder.integrated.service.ResourceMonitorService;
import com.haiphamcoder.integrated.service.ServerSentEventsService;
import com.haiphamcoder.integrated.shared.MapperUtils;
import com.haiphamcoder.integrated.shared.SseEmitters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServerSentEventsServiceImpl implements ServerSentEventsService, ApplicationListener<ContextClosedEvent> {

    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService heartbeatThreadPool = Executors.newScheduledThreadPool(1);
    private final Map<Long, Map<Integer, SseEmitters>> emitters = new HashMap<>();
    private final ResourceMonitorService resourceMonitorService;

    @PostConstruct
    public void init() {
        heartbeatThreadPool.scheduleAtFixedRate(() -> {
            emitters.forEach((userId, groupEmitters) -> {
                groupEmitters.forEach((type, emitter) -> {
                    if (type != CommonConstants.SSE_EVENT_TYPE_RESOURCE_STATUS) {
                        emitter.sendComment("heartbeat");
                    }
                });
            });
        }, 0, 5, TimeUnit.SECONDS);

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            emitters.forEach((userId, groupEmitters) -> {
                groupEmitters.forEach((type, emitter) -> {
                    if (type == CommonConstants.SSE_EVENT_TYPE_RESOURCE_STATUS) {
                        try {
                            String message = MapperUtils.objectMapper
                                    .writeValueAsString(resourceMonitorService.getResourceStatus());
                            emitter.send(CommonConstants.getSseEventName(type), message);
                        } catch (Exception e) {
                            log.error("Error sending performance data to SSE: {}", e.getMessage());
                        }
                    }
                });
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        log.info("Application context closed, shutting down SSE service...");
        shutdown();
    }

    private void shutdown() {
        // Close all SSE connections first
        emitters.forEach((userId, groupEmitters) -> {
            groupEmitters.forEach((type, emitter) -> {
                try {
                    emitter.sendComment("server-shutdown");
                    emitter.complete();
                } catch (Exception e) {
                    log.debug("Error closing SSE connection for user {} type {}: {}", userId, type, e.getMessage());
                }
            });
        });

        // Then stop the scheduler
        scheduledThreadPool.shutdown();
        try {
            if (!scheduledThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("SSE service shutdown completed");
    }

    @Override
    public SseEmitter add(Long userId, Integer type, Long timeout) {
        if (!emitters.containsKey(userId)) {
            emitters.put(userId, new HashMap<>());
        }
        if (!emitters.get(userId).containsKey(type)) {
            emitters.get(userId).put(type, new SseEmitters());
        }
        SseEmitter emitter = emitters.get(userId).get(type).add(timeout);

        // Send connected event immediately
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("connected")
                    .data(Map.of(
                            "status", "success",
                            "message", "Successfully subscribed to SSE",
                            "timestamp", System.currentTimeMillis())));
        } catch (Exception e) {
            log.debug("Error sending connected event: {}", e.getMessage());
        }

        return emitter;
    }

    @Override
    public void send(Long userId, Integer type, String data) {
        if (!emitters.containsKey(userId)) {
            return;
        }
        if (!emitters.get(userId).containsKey(type)) {
            return;
        }
        emitters.get(userId).get(type).send(CommonConstants.getSseEventName(type), data);
    }

}
