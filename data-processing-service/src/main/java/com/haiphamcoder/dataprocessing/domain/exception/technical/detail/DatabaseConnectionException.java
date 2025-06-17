package com.haiphamcoder.dataprocessing.domain.exception.technical.detail;

import com.haiphamcoder.dataprocessing.domain.exception.technical.TechnicalException;

public class DatabaseConnectionException extends TechnicalException {

    public DatabaseConnectionException(String message) {
        super(message, "TECHNICAL_001");
    }

}
