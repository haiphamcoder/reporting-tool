package com.haiphamcoder.integrated.service;

import com.haiphamcoder.integrated.domain.model.EmailDetails;
import com.haiphamcoder.integrated.domain.model.EmailTemplate;

public interface EmailService {

    void sendSimpleMessage(EmailDetails emailDetails);

    void sendMessageWithTemplate(EmailDetails emailDetails, EmailTemplate template);

    void sendHtmlMessage(EmailDetails emailDetails);

}