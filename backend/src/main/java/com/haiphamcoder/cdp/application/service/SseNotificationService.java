package com.haiphamcoder.cdp.application.service;

import com.haiphamcoder.cdp.domain.model.SseEvent;

public interface SseNotificationService {

    public void sendNotification(String id, SseEvent.Type type, Object data);
}
