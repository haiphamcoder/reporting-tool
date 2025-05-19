package com.haiphamcoder.storage.domain.exception;

import com.haiphamcoder.storage.shared.exception.BaseException;
import com.haiphamcoder.storage.shared.exception.ErrorCode;

public class SourceNotFoundException extends BaseException {

    public SourceNotFoundException() {
        super(ErrorCode.SOURCE_NOT_FOUND);
    }

    public SourceNotFoundException(String message) {
        super(ErrorCode.SOURCE_NOT_FOUND, message);
    }

    public SourceNotFoundException(String message, Throwable cause) {
        super(ErrorCode.SOURCE_NOT_FOUND, message, cause);
    }

}
