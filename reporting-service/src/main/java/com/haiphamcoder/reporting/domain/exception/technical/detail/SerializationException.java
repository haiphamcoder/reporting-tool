package com.haiphamcoder.reporting.domain.exception.technical.detail;

import com.haiphamcoder.reporting.domain.exception.technical.TechnicalException;

public class SerializationException extends TechnicalException {
    public SerializationException(String message) {
        super(message, "TECH_008");
    }
}
