package com.haiphamcoder.cdp.shared.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.haiphamcoder.cdp.shared.concurrent.exception.TaskErrorExceedLimitException;

@Slf4j
public class TaskManager {
    private final int errorThreshold;
    private final ExecutorService executor;
    private final AtomicInteger errorCounter = new AtomicInteger();
    private final long errorRetryDelay;
    private final Predicate<Throwable> errorCountingPredicate;

    public TaskManager(int errorThreshold, ExecutorService executor, long errorRetryDelay,
            Predicate<Throwable> errorCountingPredicate) {
        this.errorThreshold = errorThreshold;
        this.executor = executor;
        this.errorRetryDelay = errorRetryDelay;
        this.errorCountingPredicate = errorCountingPredicate != null ? errorCountingPredicate : t -> true;
    }

    public TaskManager(int errorThreshold, ExecutorService executor) {
        this(errorThreshold, executor, 500L, null);
    }

    public Future<?> trySubmitUntilSuccess(Runnable task, long interval) {
        Future<?> future;
        while ((future = trySubmit(task)) == null) {
            Threads.sleep(interval);
        }
        return future;
    }

    public Future<?> trySubmit(Runnable task) {
        try {
            return submit(task);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    public Future<?> submit(Runnable task) {
        if (executor.isShutdown()) {
            throw new IllegalStateException("ExecutorService has been shut down");
        }
        if (errorCounter.get() > errorThreshold) {
            Threads.sleep(errorRetryDelay);
            errorCounter.decrementAndGet();
            throw new TaskErrorExceedLimitException(
                    "Number task error " + errorCounter.get() + ", threshold " + errorThreshold);
        }
        return executor.submit(() -> {
            try {
                task.run();
                if (errorCounter.get() > 0)
                    errorCounter.decrementAndGet();
            } catch (Throwable t) {
                log.error("Execution Exception", t);
                if (errorCountingPredicate.test(t)) {
                    errorCounter.incrementAndGet();
                }
            }
        });
    }

    public void reset() {
        errorCounter.set(0);
    }

    public ExecutorService executor() {
        return this.executor;
    }

    public void shutdown() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
