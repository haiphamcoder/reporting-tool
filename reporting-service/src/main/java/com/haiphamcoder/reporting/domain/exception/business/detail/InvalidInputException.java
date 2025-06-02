package com.haiphamcoder.reporting.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.reporting.domain.exception.business.BusinessException;

public class InvalidInputException extends BusinessException{

    public InvalidInputException(String message) {
        super(message, "BUSINESS_002", HttpStatus.BAD_REQUEST);
    }
    
}
