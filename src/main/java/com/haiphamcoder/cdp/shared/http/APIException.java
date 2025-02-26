package com.haiphamcoder.cdp.shared.http;

public class APIException extends Exception {
    private int statusCode;

    public APIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "APIException{" +
                "statusCode=" + statusCode +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}

