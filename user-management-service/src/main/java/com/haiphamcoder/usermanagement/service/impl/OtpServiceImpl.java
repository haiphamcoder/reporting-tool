package com.haiphamcoder.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.haiphamcoder.usermanagement.domain.dto.OtpDto;
import com.haiphamcoder.usermanagement.domain.entity.Otp;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.mapper.OtpMapper;
import com.haiphamcoder.usermanagement.repository.OtpRepository;
import com.haiphamcoder.usermanagement.repository.UserRepository;
import com.haiphamcoder.usermanagement.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    @Override
    public OtpDto generateOtp(Long userId) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String otpCode = RandomStringUtils.randomNumeric(6);
        Otp otp = Otp.builder()
                .user(user)
                .otpCode(otpCode)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        return otpRepository.save(otp).map(OtpMapper::toDto).orElse(null);
    }

    @Override
    public OtpDto verifyOtp(String otpCode, Long userId) {
        List<Otp> otps = otpRepository.getByOtpCodeAndUserIdAndExpiredAtAfter(otpCode, userId, LocalDateTime.now());
        if (otps.isEmpty()) {
            return null;
        }
        Otp otp = otps.get(0);
        otp.setVerified(true);
        return otpRepository.save(otp).map(OtpMapper::toDto).orElse(null);
    }

}
