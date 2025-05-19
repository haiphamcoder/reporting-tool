package com.haiphamcoder.storage.shared.security.exception;

public class DecryptException extends Exception {
    public DecryptException(String message) {
        super(message);
    }

    public DecryptException(String message, Throwable cause) {
        super(message, cause);
    }

}
