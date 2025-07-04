package com.haiphamcoder.usermanagement.service;

import com.haiphamcoder.usermanagement.domain.dto.EmailDetailsDto;

public interface EmailGrpcClient {

    public void sendOtpEmail(EmailDetailsDto emailDetails);

    public void sendWelcomeEmail(EmailDetailsDto emailDetails);

}
