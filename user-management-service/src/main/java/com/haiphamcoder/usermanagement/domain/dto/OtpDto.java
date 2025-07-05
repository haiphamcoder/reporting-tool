package com.haiphamcoder.usermanagement.domain.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("otp_code")
    private String otpCode;

    @JsonProperty("expired_at")
    private LocalDateTime expiredAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("verified")
    private Boolean verified;

    @JsonProperty("user")
    private UserDto user;

    @JsonProperty("user_id")
    private Long userId;

}
