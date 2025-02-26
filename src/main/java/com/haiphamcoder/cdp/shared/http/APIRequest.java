package com.haiphamcoder.cdp.shared.http;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class APIRequest {
    private String requestId;
    private String endpoint;
    private String method;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private int timeoutMs;
    private int maxRetries;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public APIRequest(String endpoint, String method, Map<String, String> headers,
            Map<String, String> queryParams, String body, int timeoutMs, int maxRetries) {
        this.requestId = UUID.randomUUID().toString();
        this.endpoint = endpoint;
        this.method = method.toUpperCase();
        this.body = body;
        this.headers = headers;
        this.queryParams = queryParams;
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
    }

    public APIRequest(String endpoint, String method, Map<String, String> headers,
            Map<String, String> queryParams, String body) {
        this(endpoint, method, headers, queryParams, body, 5000, 3);
    }

    public APIResponse execute() {
        return executeWithRetry(maxRetries);
    }

    public CompletableFuture<APIResponse> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute);
    }

    private APIResponse executeWithRetry(int remainingRetries) {
        String url = endpoint + buildQueryString(queryParams);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeoutMs))
                .method(method, body == null? HttpRequest.BodyPublishers.noBody(): HttpRequest.BodyPublishers.ofString(body));
    
        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder.build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new APIResponse(requestId, response.statusCode(), response.body(), response.headers().map());
        } catch (Exception e) {
            if (remainingRetries == 0) {
                return new APIResponse(requestId, 500, e.getMessage(), null);
            }
            return executeWithRetry(remainingRetries - 1);
        }    
    }

    private String buildQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }
        return "?" + queryParams.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "APIRequest{" +
                "requestId='" + requestId + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", method='" + method + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                ", params=" + queryParams +
                ", timeoutSeconds=" + timeoutMs +
                ", maxRetries=" + maxRetries +
                '}';
    }
}
