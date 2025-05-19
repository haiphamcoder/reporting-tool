package com.haiphamcoder.storage.shared.security.exception;

public class GenerateKeyException extends Exception {
    public GenerateKeyException(String message) {
        super(message);
    }

    public GenerateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
