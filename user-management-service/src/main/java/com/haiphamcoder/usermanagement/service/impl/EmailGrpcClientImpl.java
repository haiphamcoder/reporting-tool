package com.haiphamcoder.usermanagement.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.haiphamcoder.integrated.proto.EmailDetailsProto;
import com.haiphamcoder.integrated.proto.EmailResponseProto;
import com.haiphamcoder.integrated.proto.EmailServiceGrpc;
import com.haiphamcoder.usermanagement.domain.dto.EmailDetailsDto;
import com.haiphamcoder.usermanagement.service.EmailGrpcClient;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailGrpcClientImpl implements EmailGrpcClient {

    private final EmailServiceGrpc.EmailServiceBlockingStub emailServiceBlockingStub;

    public EmailGrpcClientImpl(
            @Qualifier("emailIntegratedServiceChannel") ManagedChannel emailIntegratedServiceChannel) {
        this.emailServiceBlockingStub = EmailServiceGrpc.newBlockingStub(emailIntegratedServiceChannel);
    }

    @Override
    public void sendOtpEmail(EmailDetailsDto emailDetails) {
        EmailDetailsProto emailDetailsProto = convertToEmailDetailsProto(emailDetails);
        EmailResponseProto response = emailServiceBlockingStub.sendOtpEmail(emailDetailsProto);
        if (!response.getSuccess()) {
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    @Override
    public void sendWelcomeEmail(EmailDetailsDto emailDetails) {
        EmailDetailsProto emailDetailsProto = convertToEmailDetailsProto(emailDetails);
        EmailResponseProto response = emailServiceBlockingStub.sendWelcomeEmail(emailDetailsProto);
        if (!response.getSuccess()) {
            throw new RuntimeException("Failed to send welcome email");
        }
    }

    private EmailDetailsProto convertToEmailDetailsProto(EmailDetailsDto emailDetails) {
        EmailDetailsProto.Builder builder = EmailDetailsProto.newBuilder();

        if (emailDetails.getTo() != null) {
            builder.setTo(emailDetails.getTo());
        }

        if (emailDetails.getSubject() != null) {
            builder.setSubject(emailDetails.getSubject());
        }

        if (emailDetails.getBody() != null) {
            builder.setBody(emailDetails.getBody());
        }

        if (emailDetails.getFrom() != null) {
            builder.setFrom(emailDetails.getFrom());
        }

        if (emailDetails.getCc() != null) {
            emailDetails.getCc().forEach(cc -> builder.addCc(cc));
        }

        if (emailDetails.getBcc() != null) {
            emailDetails.getBcc().forEach(bcc -> builder.addBcc(bcc));
        }

        if (emailDetails.getIsHtml() != null) {
            builder.setIsHtml(emailDetails.getIsHtml());
        }

        if (emailDetails.getVariables() != null) {
            emailDetails.getVariables().forEach((key, value) -> builder.putVariables(key, value));
        }

        return builder.build();
    }
}
