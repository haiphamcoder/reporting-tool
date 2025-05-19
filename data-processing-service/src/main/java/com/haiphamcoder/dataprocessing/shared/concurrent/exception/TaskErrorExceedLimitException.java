package com.haiphamcoder.dataprocessing.shared.concurrent.exception;

public class TaskErrorExceedLimitException extends RuntimeException {
    public TaskErrorExceedLimitException(String message) {
        super(message);
    }
}
