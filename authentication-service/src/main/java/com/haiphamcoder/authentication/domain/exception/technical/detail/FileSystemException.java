package com.haiphamcoder.authentication.domain.exception.technical.detail;

import com.haiphamcoder.authentication.domain.exception.technical.TechnicalException;

public class FileSystemException extends TechnicalException {
    public FileSystemException(String message) {
        super(message, "TECH_005");
    }
}
