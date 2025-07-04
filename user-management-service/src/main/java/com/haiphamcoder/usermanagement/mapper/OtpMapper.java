package com.haiphamcoder.usermanagement.mapper;

import com.haiphamcoder.usermanagement.domain.dto.OtpDto;
import com.haiphamcoder.usermanagement.domain.entity.Otp;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OtpMapper {

    public static OtpDto toDto(Otp otp) {
        return OtpDto.builder()
                .id(otp.getId())
                .otpCode(otp.getOtpCode())
                .expiredAt(otp.getExpiredAt())
                .createdAt(otp.getCreatedAt())
                .verified(otp.getVerified())
                .user(UserMapper.toDto(otp.getUser()))
                .build();
    }

    public static Otp toEntity(OtpDto otpDto) {
        return Otp.builder()
                .id(otpDto.getId())
                .otpCode(otpDto.getOtpCode())
                .expiredAt(otpDto.getExpiredAt())
                .createdAt(otpDto.getCreatedAt())
                .verified(otpDto.getVerified())
                .user(UserMapper.toEntity(otpDto.getUser()))
                .build();
    }
}
