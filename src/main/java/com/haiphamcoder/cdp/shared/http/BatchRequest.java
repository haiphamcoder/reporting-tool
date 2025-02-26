package com.haiphamcoder.cdp.shared.http;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BatchRequest {
    private List<APIRequest> requests;

    public BatchRequest() {
        this.requests = new ArrayList<>();
    }

    public void addRequest(APIRequest request) {
        requests.add(request);
    }

    public Map<String, APIResponse> execute() {
        Map<String, APIResponse> responses = new HashMap<>();
        for (APIRequest request : requests) {
            responses.put(request.getRequestId(), request.execute());
        }
        return responses;
    }

    public CompletableFuture<Map<String, APIResponse>> executeAsync() {
        List<CompletableFuture<APIResponse>> futures = requests.stream()
                .map(APIRequest::executeAsync)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toMap(APIResponse::getRequestId, response -> response))
                );
    }

    @Override
    public String toString() {
        return "BatchRequest{" +
                "requests=" + requests +
                '}';
    }
}

