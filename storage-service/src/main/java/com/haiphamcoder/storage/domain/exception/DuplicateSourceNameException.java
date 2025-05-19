package com.haiphamcoder.storage.domain.exception;

import com.haiphamcoder.storage.shared.exception.BaseException;
import com.haiphamcoder.storage.shared.exception.ErrorCode;

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
