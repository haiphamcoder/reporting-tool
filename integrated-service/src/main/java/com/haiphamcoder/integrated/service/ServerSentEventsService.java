package com.haiphamcoder.integrated.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ServerSentEventsService {

    SseEmitter add(Long userId, Integer type, Long timeout);

    void send(Long userId, Integer type, String data);

}
