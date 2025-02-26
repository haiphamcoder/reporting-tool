package com.haiphamcoder.cdp.shared.http;

import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAPIRequest {
    @JsonProperty("request_id")
    @Builder.Default
    private String requestId = UUID.randomUUID().toString();

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("method")
    private String method;

    @JsonProperty("body")
    private String body;

    @JsonProperty("headers")
    private Map<String, String> headers;

    @JsonProperty("params")
    private Map<String, String> queryParams;

    @JsonProperty("timeout_ms")
    @Builder.Default
    private int timeoutMs = 5000;

    @JsonProperty("max_retries")
    @Builder.Default
    private int maxRetries = 3;


    // public APIRequest(String endpoint, String method, Map<String, String> headers,
    //         Map<String, String> queryParams, String body, int timeoutMs, int maxRetries) {
    //     this.requestId = UUID.randomUUID().toString();
    //     this.endpoint = endpoint;
    //     this.method = method.toUpperCase();
    //     this.body = body;
    //     this.headers = headers;
    //     this.queryParams = queryParams;
    //     this.timeoutMs = timeoutMs;
    //     this.maxRetries = maxRetries;
    // }

    // public APIRequest(String endpoint, String method, Map<String, String> headers,
    //         Map<String, String> queryParams, String body) {
    //     this(endpoint, method, headers, queryParams, body, 5000, 3);
    // }

    // public APIResponse execute() {
    //     return executeWithRetry(maxRetries);
    // }

    // public CompletableFuture<APIResponse> executeAsync() {
    //     return CompletableFuture.supplyAsync(this::execute);
    // }
}
