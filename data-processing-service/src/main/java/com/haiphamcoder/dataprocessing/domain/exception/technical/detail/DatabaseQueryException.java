package com.haiphamcoder.dataprocessing.domain.exception.technical.detail;

import com.haiphamcoder.dataprocessing.domain.exception.technical.TechnicalException;

public class DatabaseQueryException extends TechnicalException {

    public DatabaseQueryException(String message) {
        super(message, "TECHNICAL_002");
    }

}
