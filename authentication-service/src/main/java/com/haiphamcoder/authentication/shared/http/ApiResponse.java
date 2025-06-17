package com.haiphamcoder.authentication.shared.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all endpoints.
 * 
 * @param <T> Type of data being returned
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("result")
    private T data;

    @JsonProperty("instance")
    private String instance;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Create a successful response with data.
     * 
     * @param data    The data to include in the response
     * @param message Success message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Create a successful response with data and default message.
     * 
     * @param data The data to include in the response
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    /**
     * Create an error response.
     * 
     * @param message   Error message
     * @param errorCode Error code
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, Integer code, String errorCode, String instance) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setInstance(instance);
        return response;
    }

    /**
     * Create an error response from HttpStatus.
     * 
     * @param status  HTTP status
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String instance) {
        return error(message, status.value(), "Unknown", instance);
    }
}