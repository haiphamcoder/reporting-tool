package com.haiphamcoder.cdp.application.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {

    public SseEmitter createEmitter(String id);
    
}
