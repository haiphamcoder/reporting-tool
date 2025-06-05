package com.haiphamcoder.reporting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll(@CookieValue(name = "user-id") Long userId) {
        List<ChartDto> charts = chartService.getAllChartsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(charts, "Charts fetched successfully"));
    }

    @GetMapping("/{chart-id}")
    public ResponseEntity<ApiResponse<Object>> getById(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId) {
        ChartDto chart = chartService.getChartById(userId, chartId);
        return ResponseEntity.ok(ApiResponse.success(chart, "Chart fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<Object> create(@CookieValue(name = "user-id") Long userId,
            @RequestBody ChartDto chartDto) {
        ChartDto createdChart = chartService.createChart(userId, chartDto);
        return ResponseEntity.ok(ApiResponse.success(createdChart, "Chart created successfully"));
    }

    @PutMapping("/{chart-id}")
    public ResponseEntity<ApiResponse<Object>> update(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId,
            @RequestBody ChartDto chartDto) {
        ChartDto updatedChart = chartService.updateChart(userId, chartId, chartDto);
        return ResponseEntity.ok(ApiResponse.success(updatedChart, "Chart updated successfully"));
    }

    @DeleteMapping("/{chart-id}")
    public ResponseEntity<ApiResponse<Object>> delete(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId) {
        chartService.deleteChart(userId, chartId);
        return ResponseEntity.ok(ApiResponse.success(null, "Chart deleted successfully"));
    }

}
