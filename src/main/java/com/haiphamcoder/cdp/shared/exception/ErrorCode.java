package com.haiphamcoder.cdp.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization (4xx)
    UNAUTHORIZED(401, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "Access denied", HttpStatus.FORBIDDEN),
    USERNAME_OR_PASSWORD_INCORRECT(401, "Username or password is incorrect", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(401, "Invalid or expired token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(401, "Token has expired", HttpStatus.UNAUTHORIZED),

    // Validation Errors (4xx)
    INVALID_INPUT(400, "Invalid input data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD(400, "Missing required field", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT(400, "Invalid format", HttpStatus.BAD_REQUEST),
    SOURCE_NAME_EXISTED(400, "Source name already exists", HttpStatus.BAD_REQUEST),

    // Resource Not Found (4xx)
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    SOURCE_NOT_FOUND(404, "Source not found", HttpStatus.NOT_FOUND),

    // Business Logic Errors (4xx)
    DUPLICATE_RESOURCE(409, "Resource already exists", HttpStatus.CONFLICT),
    INVALID_OPERATION(400, "Invalid operation", HttpStatus.BAD_REQUEST),

    // Server Errors (5xx)
    INTERNAL_SERVER_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(503, "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_ERROR(500, "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
} 