package com.haiphamcoder.dataprocessing.domain.exception.technical.detail;

import com.haiphamcoder.dataprocessing.domain.exception.technical.TechnicalException;

public class FileNotFoundException extends TechnicalException {
    public FileNotFoundException(String filePath) {
        super(
                String.format("File not found: %s", filePath),
                "TECH_006");
    }
}
