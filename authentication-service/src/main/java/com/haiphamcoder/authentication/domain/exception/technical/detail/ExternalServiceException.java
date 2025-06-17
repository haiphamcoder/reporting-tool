package com.haiphamcoder.authentication.domain.exception.technical.detail;

import com.haiphamcoder.authentication.domain.exception.technical.TechnicalException;

public class ExternalServiceException extends TechnicalException {
    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super(message, "TECH_003");
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
