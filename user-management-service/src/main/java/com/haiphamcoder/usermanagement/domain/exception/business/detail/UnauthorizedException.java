package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, "BUSINESS_005", HttpStatus.UNAUTHORIZED);
    }
}
