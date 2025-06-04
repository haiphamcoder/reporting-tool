package com.haiphamcoder.dataprocessing.domain.exception.technical.detail;

import com.haiphamcoder.dataprocessing.domain.exception.technical.TechnicalException;

public class SerializationException extends TechnicalException {
    public SerializationException(String message) {
        super(message, "TECH_008");
    }
}
