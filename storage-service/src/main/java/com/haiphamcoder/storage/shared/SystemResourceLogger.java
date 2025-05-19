package com.haiphamcoder.storage.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SystemResourceLogger {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long interval;

    public SystemResourceLogger(@Value("${system.resource.logger.interval:30000}") long interval) {
        this.interval = interval;
        startLoggingSystemResources();
    }

    public void startLoggingSystemResources() {
        scheduler.scheduleAtFixedRate(() -> {
            logSystemResources();
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void logSystemResources() {
        try {
            // Get memory usage
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
            long heapMax = memoryMXBean.getHeapMemoryUsage().getMax() / (1024 * 1024); // MB
            long nonHeapUsed = memoryMXBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024); // MB

            // Get CPU and system information
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double systemLoadAverage = osBean.getSystemLoadAverage(); // -1 if not available
            int processors = osBean.getAvailableProcessors();

            // Log information
            SystemResource systemResource = new SystemResource(heapUsed, heapMax, nonHeapUsed, systemLoadAverage, processors);
            log.info("SystemResource: {}", systemResource.toString());

        } catch (Exception e) {
            log.error("Error while logging system resources", e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SystemResource {
        private long heapUsed;
        private long heapMax;
        private long nonHeapUsed;
        private double systemLoadAverage;
        private int processors;
    }
}
