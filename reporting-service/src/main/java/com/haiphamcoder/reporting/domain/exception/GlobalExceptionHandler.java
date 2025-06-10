package com.haiphamcoder.reporting.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.haiphamcoder.reporting.domain.exception.business.BusinessException;
import com.haiphamcoder.reporting.domain.exception.technical.TechnicalException;
import com.haiphamcoder.reporting.shared.http.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler to standardize error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        /**
         * Handle BusinessException.
         *
         * @param ex the exception
         * @return a ResponseEntity with standardized error response
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(ex.getHttpStatus())
                                .body(ApiResponse.error(ex.getMessage(), ex.getHttpStatus().value(), ex.getErrorCode(),
                                                request.getRequestURI()));
        }

        /**
         * Handle TechnicalException.
         *
         * @param ex      the exception
         * @param request the current request
         * @return a ResponseEntity with standardized error response
         */
        @ExceptionHandler(TechnicalException.class)
        public ResponseEntity<ApiResponse<Object>> handleTechnicalException(TechnicalException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(ex.getMessage(), 500, ex.getErrorCode(),
                                                request.getRequestURI()));
        }

        /**
         * Handle general exceptions.
         *
         * @param ex      the exception
         * @param request the current request
         * @return a ResponseEntity with standardized error response
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex, HttpServletRequest request) {
                String message = "An unexpected error occurred: " + ex.getMessage();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                                                message, request.getRequestURI()));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                                                ex.getMessage(), request.getRequestURI()));
        }

}
