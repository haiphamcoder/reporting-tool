package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message, "BUSINESS_006", HttpStatus.FORBIDDEN);
    }

}
