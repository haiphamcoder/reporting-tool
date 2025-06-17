package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, "BUSINESS_001", HttpStatus.BAD_REQUEST);
    }

}
