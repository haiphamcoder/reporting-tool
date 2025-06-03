package com.haiphamcoder.authentication.domain.exception.technical.detail;

import com.haiphamcoder.authentication.domain.exception.technical.TechnicalException;

public class CacheException extends TechnicalException {
    public CacheException(String message) {
        super(message, "TECH_007");
    }
}
