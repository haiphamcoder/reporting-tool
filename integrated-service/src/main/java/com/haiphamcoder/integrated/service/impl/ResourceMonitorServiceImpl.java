package com.haiphamcoder.integrated.service.impl;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haiphamcoder.integrated.domain.model.ResourceStatus;
import com.haiphamcoder.integrated.service.ResourceMonitorService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for monitoring system resources including memory, swap
 * space, and CPU usage.
 * This service runs a background thread that periodically collects and logs
 * system resource metrics.
 */
@Service
@Slf4j
public class ResourceMonitorServiceImpl implements ResourceMonitorService {

    private final OperatingSystemMXBean operatingSystemMXBean;
    private final Long interval;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new ResourceMonitorServiceImpl.
     * 
     * @param interval The time interval in milliseconds between resource status
     *                 checks.
     *                 Default value is 60000ms (1 minute) if not specified in
     *                 configuration.
     */
    public ResourceMonitorServiceImpl(@Value("${resource.monitor.interval:60000}") Long interval) {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.interval = interval;
        this.start();
    }

    /**
     * Retrieves the current system resource status.
     * 
     * @return ResourceStatus object containing:
     *         - Current timestamp in epoch milliseconds
     *         - Committed virtual memory size in bytes
     *         - Total and free swap space in bytes
     *         - Total and free physical memory in bytes
     *         - System CPU load as a value between 0.0 and 1.0
     *         - Process CPU load as a value between 0.0 and 1.0
     */
    @Override
    public ResourceStatus getResourceStatus() {
        return ResourceStatus.builder()
                .time(ZonedDateTime.now().toInstant().toEpochMilli())
                .committedVirtualMemorySize(operatingSystemMXBean.getCommittedVirtualMemorySize())
                .totalSwapSpaceSize(operatingSystemMXBean.getTotalSwapSpaceSize())
                .freeSwapSpaceSize(operatingSystemMXBean.getFreeSwapSpaceSize())
                .totalMemorySize(operatingSystemMXBean.getTotalMemorySize())
                .freeMemorySize(operatingSystemMXBean.getFreeMemorySize())
                .cpuLoad(operatingSystemMXBean.getCpuLoad())
                .processCpuLoad(operatingSystemMXBean.getProcessCpuLoad())
                .build();
    }

    /**
     * Starts the resource monitoring service in a background thread.
     * This method initiates a continuous monitoring process that:
     * 1. Collects resource metrics at specified intervals
     * 2. Converts memory values from bytes to MB for logging
     * 3. Formats and logs the resource status in a human-readable format
     * 
     * The monitoring continues until the service is shut down.
     * If any error occurs during monitoring, it will be logged but won't stop the
     * monitoring process.
     */
    public void start() {
        executorService.submit(() -> {
            while (true) {
                ResourceStatus resourceStatus = getResourceStatus();

                // Convert bytes to MB for logging
                double bytesToMB = 1024 * 1024.0;
                String resourceLog = String.format(
                        "Resource Status: Memory[Committed: %.2f MB, Total: %.2f MB, Free: %.2f MB], " +
                                "Swap[Total: %.2f MB, Free: %.2f MB], CPU[System: %.2f%%, Process: %.2f%%]",
                        resourceStatus.getCommittedVirtualMemorySize() / bytesToMB,
                        resourceStatus.getTotalMemorySize() / bytesToMB,
                        resourceStatus.getFreeMemorySize() / bytesToMB,
                        resourceStatus.getTotalSwapSpaceSize() / bytesToMB,
                        resourceStatus.getFreeSwapSpaceSize() / bytesToMB,
                        resourceStatus.getCpuLoad() * 100,
                        resourceStatus.getProcessCpuLoad() * 100);
                log.info(resourceLog);

                Thread.sleep(interval);
            }
        });
    }
}
