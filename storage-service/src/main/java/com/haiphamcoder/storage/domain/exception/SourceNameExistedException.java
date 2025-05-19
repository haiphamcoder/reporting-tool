package com.haiphamcoder.storage.domain.exception;

import com.haiphamcoder.storage.shared.exception.BaseException;
import com.haiphamcoder.storage.shared.exception.ErrorCode;

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
