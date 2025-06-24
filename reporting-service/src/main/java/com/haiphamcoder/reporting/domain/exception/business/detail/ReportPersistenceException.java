package com.haiphamcoder.reporting.domain.exception.business.detail;

import org.springframework.http.HttpStatus;
import com.haiphamcoder.reporting.domain.exception.business.BusinessException;

public class ReportPersistenceException extends BusinessException {
    public ReportPersistenceException(String message) {
        super(message, "BUSINESS_009", HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 