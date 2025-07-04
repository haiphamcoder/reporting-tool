package com.haiphamcoder.usermanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.haiphamcoder.usermanagement.domain.entity.Otp;

public interface OtpRepository {

    Optional<Otp> getById(Long id);

    Optional<Otp> save(Otp otp);

    List<Otp> getByOtpCodeAndUserIdAndExpiredAtAfter(String otpCode, Long userId, LocalDateTime expiredAt);

}
