package com.haiphamcoder.storage.shared.concurrent.exception;

public class TaskErrorExceedLimitException extends RuntimeException {
    public TaskErrorExceedLimitException(String message) {
        super(message);
    }
}
