package com.haiphamcoder.dataprocessing.domain.exception;

import com.haiphamcoder.dataprocessing.shared.exception.BaseException;
import com.haiphamcoder.dataprocessing.shared.exception.ErrorCode;

public class ConnectorTypeNotSupportException extends BaseException {
    public ConnectorTypeNotSupportException() {
        super(ErrorCode.CONNECTOR_TYPE_NOT_SUPPORTED);
    }

    public ConnectorTypeNotSupportException(String message) {
        super(ErrorCode.CONNECTOR_TYPE_NOT_SUPPORTED, message);
    }

    public ConnectorTypeNotSupportException(String message, Throwable cause) {
        super(ErrorCode.CONNECTOR_TYPE_NOT_SUPPORTED, message, cause);
    }
}
