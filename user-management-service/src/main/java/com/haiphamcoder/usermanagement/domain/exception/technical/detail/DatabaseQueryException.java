package com.haiphamcoder.usermanagement.domain.exception.technical.detail;

import com.haiphamcoder.usermanagement.domain.exception.technical.TechnicalException;

public class DatabaseQueryException extends TechnicalException {

    public DatabaseQueryException(String message) {
        super(message, "TECHNICAL_002");
    }

}
