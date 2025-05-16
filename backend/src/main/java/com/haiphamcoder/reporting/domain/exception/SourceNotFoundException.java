package com.haiphamcoder.reporting.domain.exception;

import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.exception.ErrorCode;

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
