package com.haiphamcoder.usermanagement.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.usermanagement.domain.entity.Otp;
import com.haiphamcoder.usermanagement.repository.OtpRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface OtpJpaRepository extends JpaRepository<Otp, Long> {

    List<Otp> findByOtpCodeAndUserIdAndExpiredAtAfter(String otpCode, Long userId, LocalDateTime expiredAt);

}

@Component
@RequiredArgsConstructor
public class OtpRepositoryImpl implements OtpRepository {

    private final OtpJpaRepository otpJpaRepository;

    @Override
    public Optional<Otp> getById(Long id) {
        return otpJpaRepository.findById(id);
    }

    @Override
    public Optional<Otp> save(Otp otp) {
        return Optional.of(otpJpaRepository.save(otp));
    }

    @Override
    public List<Otp> getByOtpCodeAndUserIdAndExpiredAtAfter(String otpCode, Long userId, LocalDateTime expiredAt) {
        return otpJpaRepository.findByOtpCodeAndUserIdAndExpiredAtAfter(otpCode, userId, expiredAt);
    }
}
