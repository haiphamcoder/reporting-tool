package com.haiphamcoder.cdp.domain.exception;

import com.haiphamcoder.cdp.shared.exception.BaseException;
import com.haiphamcoder.cdp.shared.exception.ErrorCode;

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
