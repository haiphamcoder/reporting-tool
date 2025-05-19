package com.haiphamcoder.dataprocessing.domain.exception;

import com.haiphamcoder.dataprocessing.shared.exception.BaseException;
import com.haiphamcoder.dataprocessing.shared.exception.ErrorCode;

public class MissingRequiredFieldException extends BaseException {

    public MissingRequiredFieldException() {
        super(ErrorCode.MISSING_REQUIRED_FIELD);
    }

    public MissingRequiredFieldException(String missingField) {
        super(ErrorCode.MISSING_REQUIRED_FIELD, missingField + " is required");
    }
}
