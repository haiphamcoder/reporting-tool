package com.haiphamcoder.cdp.shared;

import org.springframework.http.HttpStatus;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiResponseFactory {
    public static <T> ApiResponse<T> createSuccessResponse(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "Success", data, null, null);
    }

    public static <T> ApiResponse<T> createUnauthorizedResponse(String message) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), message, null, null, null);
    }
}
