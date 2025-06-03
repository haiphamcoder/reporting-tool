package com.haiphamcoder.usermanagement.domain.exception.technical.detail;

import com.haiphamcoder.usermanagement.domain.exception.technical.TechnicalException;

public class ExternalServiceTimeoutException extends TechnicalException {
    public ExternalServiceTimeoutException(String serviceName) {
        super(
                String.format("Timeout while calling service: %s", serviceName),
                "TECH_004");
    }
}
