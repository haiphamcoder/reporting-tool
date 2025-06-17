package com.haiphamcoder.authentication.domain.exception.technical;

public class TechnicalException extends RuntimeException {

    private final String errorCode;

    protected TechnicalException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
