package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(String message) {
        super(message, "BUSINESS_008", HttpStatus.TOO_MANY_REQUESTS);
    }
}
