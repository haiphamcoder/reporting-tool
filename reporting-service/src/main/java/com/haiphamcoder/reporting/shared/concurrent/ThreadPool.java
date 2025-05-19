package com.haiphamcoder.reporting.shared.concurrent;

import lombok.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ExecutorService executorService;

    private ThreadPool(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public static class Builder {
        private boolean daemon = true;
        private String namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        private int coreSize = Runtime.getRuntime().availableProcessors(); // Giá trị mặc định dựa trên CPU
        private int maxSize = coreSize * 2; // Giá trị mặc định gấp đôi coreSize
        private int queueSize = 0;
        private long keepAliveTimeout = 60L;
        private TimeUnit timeUnit = TimeUnit.SECONDS;
        private ThreadFactory threadFactory;
        private BlockingQueue<Runnable> workQueue;
        private RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.AbortPolicy();

        public Builder setDaemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public Builder setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
            return this;
        }

        public Builder setCoreSize(int coreSize) {
            this.coreSize = coreSize;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder setQueueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
        }

        public Builder setKeepAliveTimeout(long keepAliveTimeout) {
            this.keepAliveTimeout = keepAliveTimeout;
            return this;
        }

        public Builder setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder setThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder setWorkQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public Builder setRejectionHandler(RejectedExecutionHandler rejectionHandler) {
            this.rejectionHandler = rejectionHandler != null ? rejectionHandler : new ThreadPoolExecutor.AbortPolicy();
            return this;
        }

        public ThreadPool build() {
            // Validate parameters
            if (coreSize < 0 || maxSize < 0 || keepAliveTimeout < 0) {
                throw new IllegalArgumentException("coreSize, maxSize, and keepAliveTimeout must be non-negative");
            }
            if (maxSize < coreSize) {
                maxSize = coreSize; // Đảm bảo maxSize không nhỏ hơn coreSize
            }

            // Initialize work queue if not specified
            if (workQueue == null) {
                workQueue = queueSize <= 0 ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(queueSize);
            }

            // Initialize ThreadFactory if not specified
            if (threadFactory == null) {
                threadFactory = new DataCollectionThreadFactory(namePrefix, daemon);
            }

            // Create ThreadPoolExecutor
            ExecutorService executor = new ThreadPoolExecutor(
                    coreSize,
                    maxSize,
                    keepAliveTimeout,
                    timeUnit,
                    workQueue,
                    threadFactory,
                    rejectionHandler);

            return new ThreadPool(executor);
        }
    }

    static class DataCollectionThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final boolean daemon;

        DataCollectionThreadFactory(String prefix, boolean daemon) {
            this.namePrefix = prefix + "-";
            this.daemon = daemon;
        }

        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(daemon);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}