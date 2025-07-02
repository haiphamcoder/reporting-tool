package com.haiphamcoder.integrated.service;

import java.util.Map;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);

    void sendMessageWithTemplate(String to, String subject, String templateName, Map<String, Object> variables);

}