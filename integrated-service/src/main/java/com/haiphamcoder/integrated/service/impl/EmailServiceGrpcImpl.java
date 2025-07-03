package com.haiphamcoder.integrated.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.integrated.domain.model.EmailDetails;
import com.haiphamcoder.integrated.domain.model.EmailTemplate;
import com.haiphamcoder.integrated.proto.*;
import com.haiphamcoder.integrated.service.EmailService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceGrpcImpl extends EmailServiceGrpc.EmailServiceImplBase {
    private final EmailService emailService;

    @Override
    public void sendOtpEmail(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(Arrays.asList(request.getTo()));
        emailDetails.setSubject(request.getSubject());
        emailDetails.setText(request.getBody());
        emailDetails.setFrom(request.getFrom());
        emailDetails.setCc(request.getCcList());
        emailDetails.setBcc(request.getBccList());
        emailDetails.setHtml(request.getIsHtml());
        emailDetails.setVariables(request.getVariablesMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue())));
        emailService.sendMessageWithTemplate(emailDetails, EmailTemplate.OTP);
        EmailResponse response = EmailResponse.newBuilder().setSuccess(true).setMessage("Email sent successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sendWelcomeEmail(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(Arrays.asList(request.getTo()));
        emailDetails.setSubject(request.getSubject());
        emailDetails.setText(request.getBody());
        emailDetails.setFrom(request.getFrom());
        emailDetails.setCc(request.getCcList());
        emailDetails.setBcc(request.getBccList());
        emailDetails.setHtml(request.getIsHtml());
        emailDetails.setVariables(request.getVariablesMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue())));
        emailService.sendMessageWithTemplate(emailDetails, EmailTemplate.WELCOME);
        EmailResponse response = EmailResponse.newBuilder().setSuccess(true).setMessage("Email sent successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
