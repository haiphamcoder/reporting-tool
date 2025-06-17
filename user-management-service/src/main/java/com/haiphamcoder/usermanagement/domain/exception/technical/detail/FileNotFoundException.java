package com.haiphamcoder.usermanagement.domain.exception.technical.detail;

import com.haiphamcoder.usermanagement.domain.exception.technical.TechnicalException;

public class FileNotFoundException extends TechnicalException {
    public FileNotFoundException(String filePath) {
        super(
                String.format("File not found: %s", filePath),
                "TECH_006");
    }
}
