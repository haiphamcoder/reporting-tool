package com.haiphamcoder.reporting.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.reporting.domain.exception.business.BusinessException;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(resourceType + " not found with id: " + resourceId, "BUSINESS_003", HttpStatus.NOT_FOUND);
    }

}
