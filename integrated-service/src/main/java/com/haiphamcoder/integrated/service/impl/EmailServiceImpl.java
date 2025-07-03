package com.haiphamcoder.integrated.service.impl;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.haiphamcoder.integrated.domain.model.EmailDetails;
import com.haiphamcoder.integrated.domain.model.EmailTemplate;
import com.haiphamcoder.integrated.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendSimpleMessage(EmailDetails emailDetails) {

        if (emailDetails.getTo() == null || emailDetails.getTo().isEmpty()) {
            throw new IllegalArgumentException("To is required");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailDetails.getFrom());
        message.setTo(emailDetails.getTo().toArray(new String[0]));
        message.setSubject(emailDetails.getSubject());
        message.setText(emailDetails.getText());
        message.setSentDate(emailDetails.getSentDate());
        message.setReplyTo(emailDetails.getReplyTo());
        message.setCc(emailDetails.getCc().toArray(new String[0]));
        message.setBcc(emailDetails.getBcc().toArray(new String[0]));
        mailSender.send(message);
    }

    @Override
    public void sendMessageWithTemplate(EmailDetails emailDetails, EmailTemplate template) {
        if (emailDetails.getTo() == null || emailDetails.getTo().isEmpty()) {
            throw new IllegalArgumentException("To is required");
        }

        if (template == null) {
            throw new IllegalArgumentException("Template is required");
        }

        String templateContent = templateEngine.process(template.getTemplateFile(),
                new Context(Locale.getDefault(), emailDetails.getVariables()));

        emailDetails.setText(templateContent);
        sendHtmlMessage(emailDetails);
    }

    @Override
    public void sendHtmlMessage(EmailDetails emailDetails) {
        if (emailDetails.getTo() == null || emailDetails.getTo().isEmpty()) {
            throw new IllegalArgumentException("To is required");
        }

        if (emailDetails.getSubject() == null || emailDetails.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailDetails.getFrom());
            helper.setTo(emailDetails.getTo().toArray(new String[0]));
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailDetails.getText(), true);
            if (emailDetails.getCc() != null && !emailDetails.getCc().isEmpty()) {
                helper.setCc(emailDetails.getCc().toArray(new String[0]));
            }
            if (emailDetails.getBcc() != null && !emailDetails.getBcc().isEmpty()) {
                helper.setBcc(emailDetails.getBcc().toArray(new String[0]));
            }
            if (emailDetails.getSentDate() != null) {
                helper.setSentDate(emailDetails.getSentDate());
            }
            if (emailDetails.getReplyTo() != null) {
                helper.setReplyTo(emailDetails.getReplyTo());
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
