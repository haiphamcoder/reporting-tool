package com.haiphamcoder.reporting.application.service;

import com.haiphamcoder.reporting.domain.model.SseEvent;

public interface SseNotificationService {

    public void sendNotification(String id, SseEvent.Type type, Object data);
}
