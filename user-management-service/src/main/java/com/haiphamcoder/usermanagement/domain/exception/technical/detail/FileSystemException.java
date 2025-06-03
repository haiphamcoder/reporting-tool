package com.haiphamcoder.usermanagement.domain.exception.technical.detail;

import com.haiphamcoder.usermanagement.domain.exception.technical.TechnicalException;

public class FileSystemException extends TechnicalException {
    public FileSystemException(String message) {
        super(message, "TECH_005");
    }
}
