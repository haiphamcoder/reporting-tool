package com.haiphamcoder.dataprocessing.domain.exception.technical.detail;

import com.haiphamcoder.dataprocessing.domain.exception.technical.TechnicalException;

public class ConfigurationException extends TechnicalException {
    public ConfigurationException(String message) {
        super(message, "TECH_009");
    }
}
