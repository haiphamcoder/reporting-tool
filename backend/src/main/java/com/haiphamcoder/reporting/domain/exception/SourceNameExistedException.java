package com.haiphamcoder.reporting.domain.exception;

import com.haiphamcoder.reporting.shared.exception.BaseException;
import com.haiphamcoder.reporting.shared.exception.ErrorCode;

public class SourceNameExistedException extends BaseException {

    public SourceNameExistedException() {
        super(ErrorCode.SOURCE_NAME_EXISTED);
    }

    public SourceNameExistedException(String message) {
        super(ErrorCode.SOURCE_NAME_EXISTED, message);
    }

    public SourceNameExistedException(String message, Throwable cause) {
        super(ErrorCode.SOURCE_NAME_EXISTED, message, cause);
    }

}
