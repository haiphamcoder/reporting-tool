package com.haiphamcoder.cdp.shared.security.exception;

import com.haiphamcoder.cdp.shared.exception.BaseException;
import com.haiphamcoder.cdp.shared.exception.ErrorCode;

/**
 * Exception thrown when authentication fails due to incorrect username or password.
 */
public class UsernameOrPasswordIncorrectException extends BaseException {

    public UsernameOrPasswordIncorrectException() {
        super(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT);
    }

    public UsernameOrPasswordIncorrectException(String message) {
        super(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT, message);
    }

    public UsernameOrPasswordIncorrectException(String message, Throwable cause) {
        super(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT, message, cause);
    }
} 