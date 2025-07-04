package com.haiphamcoder.usermanagement.service;

import com.haiphamcoder.usermanagement.domain.dto.OtpDto;

public interface OtpService {

    OtpDto generateOtp(Long userId);

    OtpDto verifyOtp(String otpCode, Long userId);

}
