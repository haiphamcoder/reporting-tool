package com.haiphamcoder.dataprocessing.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.dataprocessing.domain.exception.business.BusinessException;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message, "BUSINESS_006", HttpStatus.FORBIDDEN);
    }

}
