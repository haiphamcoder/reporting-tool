package com.haiphamcoder.dataprocessing.domain.exception;

import com.haiphamcoder.dataprocessing.shared.exception.BaseException;
import com.haiphamcoder.dataprocessing.shared.exception.ErrorCode;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(ErrorCode.USER_NOT_FOUND, message, cause);
    }
}