package com.haiphamcoder.cdp.shared.http;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class HttpCaller {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static RestAPIResponse execute(RestAPIRequest request) {
        HttpRequest httpRequest = buildRequest(request);
        int remainingRetries = request.getMaxRetries();
        while (remainingRetries > 0) {
            try {
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() < 500) {
                    return new RestAPIResponse(request.getRequestId(), response.statusCode(), response.body(),
                            response.headers().map());
                }
            } catch (IOException | InterruptedException e) {
                log.error("Failed to send HTTP request", e);
            }
            remainingRetries--;
        }
        return new RestAPIResponse(request.getRequestId(), 500, null, null);
    }

    public static CompletableFuture<RestAPIResponse> executeAsync(RestAPIRequest request) {
        HttpRequest httpRequest = buildRequest(request);
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(
                        response -> new RestAPIResponse(request.getRequestId(), response.statusCode(), response.body(),
                                response.headers().map()))
                .exceptionally(e -> {
                    log.error("Failed to send HTTP request", e);
                    return new RestAPIResponse(request.getRequestId(), 500, null, null);
                });
    }

    private static String buildQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }
        return "?" + queryParams.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    private static HttpRequest buildRequest(RestAPIRequest request) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(request.getEndpoint() + buildQueryString(request.getQueryParams())))
                .timeout(Duration.ofMillis(request.getTimeoutMs()))
                .method(request.getMethod(), request.getBody() == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(request.getBody()));

        Map<String, String> headers = request.getHeaders();
        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }
        return requestBuilder.build();
    }
}
