package com.haiphamcoder.reporting.domain.exception;

import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.exception.ErrorCode;

public class DuplicateSourceNameException extends BaseException {

    public DuplicateSourceNameException() {
        super(ErrorCode.DUPLICATE_SOURCE_NAME);
    }

    public DuplicateSourceNameException(String message) {
        super(ErrorCode.DUPLICATE_SOURCE_NAME, message);
    }

    public DuplicateSourceNameException(String message, Throwable cause) {
        super(ErrorCode.DUPLICATE_SOURCE_NAME, message, cause);
    }
    
}
