package com.haiphamcoder.authentication.domain.exception.technical.detail;

import com.haiphamcoder.authentication.domain.exception.technical.TechnicalException;

public class ConfigurationException extends TechnicalException {
    public ConfigurationException(String message) {
        super(message, "TECH_009");
    }
}
