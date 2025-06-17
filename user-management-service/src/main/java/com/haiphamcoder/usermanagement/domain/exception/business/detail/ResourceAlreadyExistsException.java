package com.haiphamcoder.usermanagement.domain.exception.business.detail;

import org.springframework.http.HttpStatus;

import com.haiphamcoder.usermanagement.domain.exception.business.BusinessException;

public class ResourceAlreadyExistsException extends BusinessException {

    public ResourceAlreadyExistsException(String resourceType, String identifier) {
        super(resourceType + " already exists with identifier: " + identifier, "BUSINESS_004", HttpStatus.CONFLICT);
    }

}
