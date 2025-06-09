package com.haiphamcoder.dataprocessing.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import org.json.JSONObject;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;
import com.haiphamcoder.dataprocessing.domain.model.ChartSchedule;
import com.haiphamcoder.dataprocessing.service.QueryService;
import com.haiphamcoder.dataprocessing.service.StorageService;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {
    private final StorageService storageService;
    private final TaskScheduler taskScheduler = new ConcurrentTaskScheduler(
            Executors.newScheduledThreadPool(10));
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long, ChartSchedule> schedules = new ConcurrentHashMap<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean executeAndSaveQuery(Long chartId, JsonNode queryOption) {
        log.info("Executing query for chart {} with options: {}", chartId, queryOption);
        if (!validateQuery(queryOption)) {
            log.error("Invalid query options for chart {}", chartId);
            return false;
        }

        try {
            JsonNode optimizedQuery = optimizeQuery(queryOption);

            // Thực thi query và lấy kết quả
            List<JSONObject> queryResults = executeQuery(optimizedQuery);

            // Tạo SourceDto để lưu dữ liệu
            SourceDto sourceDto = SourceDto.builder()
                    .id(chartId)
                    .name("chart_" + chartId)
                    .config((ObjectNode) optimizedQuery)
                    .build();

            // Lưu kết quả vào TiDB thông qua StorageService
            storageService.batchInsert(sourceDto, queryResults);

            updateScheduleStatus(chartId, "SUCCESS");
            return true;
        } catch (Exception e) {
            log.error("Error executing query for chart {}: {}", chartId, e.getMessage());
            updateScheduleStatus(chartId, "FAILED");
            return false;
        }
    }

    private List<JSONObject> executeQuery(JsonNode queryOption) {
        // TODO: Implement query execution logic here
        // This is just a placeholder implementation
        List<JSONObject> results = new ArrayList<>();
        JSONObject sampleData = new JSONObject();
        sampleData.put("value", 100);
        sampleData.put("category", "Sample");
        results.add(sampleData);
        return results;
    }

    @Override
    public ChartSchedule scheduleQuery(Long chartId, String cronExpression) {
        log.info("Scheduling query for chart {} with cron expression: {}", chartId, cronExpression);
        ChartSchedule schedule = ChartSchedule.builder()
                .chartId(chartId)
                .cronExpression(cronExpression)
                .enabled(true)
                .lastExecutionTime(null)
                .nextExecutionTime(calculateNextExecutionTime(cronExpression))
                .status("SCHEDULED")
                .build();

        schedules.put(chartId, schedule);
        scheduleTask(chartId, cronExpression);
        return schedule;
    }

    @Override
    public ChartSchedule updateSchedule(Long chartId, String cronExpression) {
        log.info("Updating schedule for chart {} with new cron expression: {}", chartId, cronExpression);
        ChartSchedule schedule = schedules.get(chartId);
        if (schedule == null) {
            return scheduleQuery(chartId, cronExpression);
        }

        cancelScheduledTask(chartId);
        schedule.setCronExpression(cronExpression);
        schedule.setNextExecutionTime(calculateNextExecutionTime(cronExpression));
        scheduleTask(chartId, cronExpression);
        return schedule;
    }

    @Override
    public void disableSchedule(Long chartId) {
        log.info("Disabling schedule for chart {}", chartId);
        ChartSchedule schedule = schedules.get(chartId);
        if (schedule != null) {
            schedule.setEnabled(false);
            cancelScheduledTask(chartId);
        }
    }

    @Override
    public void enableSchedule(Long chartId) {
        log.info("Enabling schedule for chart {}", chartId);
        ChartSchedule schedule = schedules.get(chartId);
        if (schedule != null) {
            schedule.setEnabled(true);
            scheduleTask(chartId, schedule.getCronExpression());
        }
    }

    @Override
    public ChartSchedule getSchedule(Long chartId) {
        return schedules.get(chartId);
    }

    @Override
    public List<ChartSchedule> getAllActiveSchedules() {
        List<ChartSchedule> activeSchedules = new ArrayList<>();
        for (ChartSchedule schedule : schedules.values()) {
            if (schedule.getEnabled()) {
                activeSchedules.add(schedule);
            }
        }
        return activeSchedules;
    }

    @Override
    public boolean validateQuery(JsonNode queryOption) {
        if (queryOption == null || queryOption.isNull()) {
            log.error("Query option is null");
            return false;
        }

        try {
            // Kiểm tra các trường bắt buộc
            if (!queryOption.has("source") || !queryOption.has("fields")) {
                log.error("Missing required fields in query options");
                return false;
            }

            // Kiểm tra source
            JsonNode source = queryOption.get("source");
            if (!source.has("id") || !source.has("type")) {
                log.error("Invalid source configuration");
                return false;
            }

            // Kiểm tra fields
            JsonNode fields = queryOption.get("fields");
            if (!fields.isArray() || fields.size() == 0) {
                log.error("Invalid fields configuration");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating query: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public JsonNode optimizeQuery(JsonNode queryOption) {
        if (!validateQuery(queryOption)) {
            return queryOption;
        }

        try {
            ObjectNode optimizedQuery = MapperUtils.objectMapper.createObjectNode();

            // Copy các trường cơ bản
            optimizedQuery.set("source", queryOption.get("source"));
            optimizedQuery.set("fields", queryOption.get("fields"));

            // Tối ưu hóa điều kiện lọc nếu có
            if (queryOption.has("filters")) {
                JsonNode filters = queryOption.get("filters");
                // TODO: Implement filter optimization logic here
                optimizedQuery.set("filters", filters);
            }

            // Tối ưu hóa sắp xếp nếu có
            if (queryOption.has("sort")) {
                JsonNode sort = queryOption.get("sort");
                // TODO: Implement sort optimization logic here
                optimizedQuery.set("sort", sort);
            }

            // Tối ưu hóa phân trang nếu có
            if (queryOption.has("pagination")) {
                JsonNode pagination = queryOption.get("pagination");
                // TODO: Implement pagination optimization logic here
                optimizedQuery.set("pagination", pagination);
            }

            return optimizedQuery;
        } catch (Exception e) {
            log.error("Error optimizing query: {}", e.getMessage());
            return queryOption;
        }
    }

    private void scheduleTask(Long chartId, String cronExpression) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> executeScheduledQuery(chartId),
                new CronTrigger(cronExpression));
        scheduledTasks.put(chartId, scheduledTask);
    }

    private void cancelScheduledTask(Long chartId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(chartId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }

    private void executeScheduledQuery(Long chartId) {
        ChartSchedule schedule = schedules.get(chartId);
        if (schedule != null && schedule.getEnabled()) {
            schedule.setLastExecutionTime(LocalDateTime.now().format(dateFormatter));
            schedule.setNextExecutionTime(calculateNextExecutionTime(schedule.getCronExpression()));
            // TODO: Get query options for this chart and execute
            // chartGrpcClient.getChartById(chartId) để lấy query options
        }
    }

    private String calculateNextExecutionTime(String cronExpression) {
        // TODO: Implement next execution time calculation based on cron expression
        return LocalDateTime.now().plusHours(1).format(dateFormatter);
    }

    private void updateScheduleStatus(Long chartId, String status) {
        ChartSchedule schedule = schedules.get(chartId);
        if (schedule != null) {
            schedule.setStatus(status);
            schedule.setLastExecutionTime(LocalDateTime.now().format(dateFormatter));
        }
    }
}
