package com.haiphamcoder.reporting.shared.security.exception;

public class EncryptException extends Exception {
    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }

}
