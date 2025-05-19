package com.haiphamcoder.dataprocessing.domain.exception;

import com.haiphamcoder.dataprocessing.shared.exception.BaseException;
import com.haiphamcoder.dataprocessing.shared.exception.ErrorCode;

public class PermissionDeniedException extends BaseException {
    public PermissionDeniedException() {
        super(ErrorCode.PERMISSION_DENIED);
    }

    public PermissionDeniedException(String message) {
        super(ErrorCode.PERMISSION_DENIED, message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(ErrorCode.PERMISSION_DENIED, message, cause);
    }
}
