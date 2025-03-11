package com.haiphamcoder.cdp.adapter.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.haiphamcoder.cdp.application.service.EmitterService;
import com.haiphamcoder.cdp.application.service.SseNotificationService;
import com.haiphamcoder.cdp.domain.model.SseEvent;
import com.haiphamcoder.cdp.shared.StringUtils;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/sse")
public class SseController {

    private final EmitterService emitterService;
    private final SseNotificationService sseNotificationService;

    @GetMapping(path = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String id) {
        log.info("Subscribing to id {}", id);
        if (StringUtils.isNullOrEmpty(id)) {
            log.error("Id is null or empty");
            return null;
        }
        SseEmitter emitter = emitterService.createEmitter(id);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(200);
                emitter.send(SseEmitter.event().name(SseEvent.Type.CONFIRM.getValue()).data("connected!"));
            } catch (IOException e) {
                log.error("Error sending confirm to emitter", e);
            } catch (InterruptedException e) {
                log.error("Thread interrupted", e);
            }
        });
        thread.start();

        return emitter;
    }

    @PostMapping(path = "/publish/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishEvent(@PathVariable String id, @RequestBody Object data) {
        log.info("Publishing event to id {}", id);
        if (StringUtils.isNullOrEmpty(id)) {
            log.error("Id is null or empty");
            return;
        }
        sseNotificationService.sendNotification(id, SseEvent.Type.MESSAGE, data);
    }
}
