package com.haiphamcoder.reporting.domain.exception.technical.detail;

import com.haiphamcoder.reporting.domain.exception.technical.TechnicalException;

public class DatabaseQueryException extends TechnicalException {

    public DatabaseQueryException(String message) {
        super(message, "TECHNICAL_002");
    }

}
