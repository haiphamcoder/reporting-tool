package com.haiphamcoder.integrated.shared.http;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.integrated.shared.exception.BaseException;

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

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("headers")
    private Map<String, List<String>> headers;

    @UtilityClass
    public static class ResponseFactory {
        public static <T> RestAPIResponse<T> createResponse(T data) {
            return new RestAPIResponse<>(null, null, data, null);
        }

        public static <T> RestAPIResponse<T> createResponse(String message) {
            return new RestAPIResponse<>(null, message, null, null);
        }

        public static <T> RestAPIResponse<T> createResponse(String message, T data) {
            return new RestAPIResponse<>(null, message, data, null);
        }

        public static <T> RestAPIResponse<T> createResponse(BaseException exception) {
            return new RestAPIResponse<>(null, exception.getErrorCode().getMessage(), null, null);
        }

        public static RestAPIResponse<String> internalServerErrorResponse(){
            return new RestAPIResponse<>(null, "Internal server error", null, null);
        }

    }
}
