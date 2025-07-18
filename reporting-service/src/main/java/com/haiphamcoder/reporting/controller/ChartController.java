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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ChartDto.UserChartPermission;
import com.haiphamcoder.reporting.domain.model.request.CreateChartRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareChartRequest;
import com.haiphamcoder.reporting.domain.model.response.GetAllChartsResponse;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
        Pair<List<ChartDto>, Metadata> charts = chartService.getAllChartsByUserId(userId, search, page, limit);
        GetAllChartsResponse response = GetAllChartsResponse.builder()
                .data(charts.getFirst().stream().map(chart -> GetAllChartsResponse.Record.builder()
                        .id(chart.getId())
                        .name(chart.getName())
                        .description(chart.getDescription())
                        .owner(chart.getOwner())
                        .type(chart.getConfig().getType().getValue())
                        .config(chart.getConfig())
                        .sqlQuery(chart.getSqlQuery())
                        .canEdit(chart.getCanEdit())
                        .canShare(chart.getCanShare())
                        .createdAt(chart.getCreatedAt())
                        .updatedAt(chart.getModifiedAt())
                        .build())
                        .toList())
                .metadata(charts.getSecond())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Charts fetched successfully"));
    }

    @GetMapping("/{chart-id}/share")
    public ResponseEntity<ApiResponse<Object>> getShare(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId) {
        List<UserChartPermission> shareChart = chartService.getShareChart(userId, chartId);
        return ResponseEntity.ok(ApiResponse.success(shareChart, "Share chart fetched successfully"));
    }

    @PostMapping("/{chart-id}/share")
    public ResponseEntity<ApiResponse<Object>> share(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId,
            @RequestBody ShareChartRequest shareChartRequest) {
        chartService.updateShareChart(userId, chartId, shareChartRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Chart shared successfully"));
    }

    @GetMapping("/{chart-id}/clone")
    public ResponseEntity<ApiResponse<Object>> clone(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId) {
        ChartDto clonedChart = chartService.cloneChart(userId, chartId);
        return ResponseEntity.ok(ApiResponse.success(clonedChart, "Chart cloned successfully"));
    }

    @GetMapping("/{chart-id}")
    public ResponseEntity<ApiResponse<Object>> getById(@CookieValue(name = "user-id") Long userId,
            @PathVariable("chart-id") Long chartId) {
        ChartDto chart = chartService.getChartById(userId, chartId);
        return ResponseEntity.ok(ApiResponse.success(chart, "Chart fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<Object> create(@CookieValue(name = "user-id") Long userId,
            @RequestBody CreateChartRequest request) {
        ChartDto createdChart = chartService.createChart(userId, request);
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
