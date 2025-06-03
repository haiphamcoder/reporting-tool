package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class BusinessRuleViolationException extends BusinessException {

    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_007", HttpStatus.BAD_REQUEST);
    }
}
