package com.haiphamcoder.cdp.shared.http;

import java.util.List;
import java.util.Map;

public class APIResponse {
    private String requestId;
    private int statusCode;
    private String data;
    private Map<String, List<String>> headers;

    public APIResponse(String requestId, int statusCode, String data, Map<String, List<String>> headers) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getData() {
        return data;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    @Override
    public String toString() {
        return "APIResponse{" +
                "requestId='" + requestId + '\'' +
                ", statusCode=" + statusCode +
                ", data='" + data + '\'' +
                '}';
    }
}
