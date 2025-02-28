package com.haiphamcoder.cdp.shared.http;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAPIResponse<T> {
    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("headers")
    private Map<String, List<String>> headers;

    @UtilityClass
    public static class ResponseFactory {
        public static <T> RestAPIResponse<T> createSuccessResponse(T data) {
            return new RestAPIResponse<>(null, 200, "Success", data, null);
        }

        public static <T> RestAPIResponse<T> createUnauthorizedResponse(String message) {
            return new RestAPIResponse<>(null, 401, message, null, null);
        }

        public static <T> RestAPIResponse<T> createErrorResponse(String message) {
            return new RestAPIResponse<>(null, 500, message, null, null);
        }
    }
}
