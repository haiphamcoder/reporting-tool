package com.haiphamcoder.reporting.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.model.response.GetAllReportsResponse;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.service.ReportService;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
        Pair<List<ReportDto>, Metadata> reports = reportService.getAllReportsByUserId(userId, page, limit);
        GetAllReportsResponse response = GetAllReportsResponse.builder()
                .data(reports.getFirst().stream().map(report -> GetAllReportsResponse.Record.builder()
                        .id(report.getId().toString())
                        .name(report.getName())
                        .description(report.getDescription())
                        .createdAt(report.getCreatedAt())
                        .updatedAt(report.getModifiedAt())
                        .build())
                        .collect(Collectors.toList()))
                .metadata(reports.getSecond())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Reports fetched successfully"));
    }

    @GetMapping("/{report-id}")
    public ResponseEntity<ApiResponse<Object>> getById(@CookieValue(name = "user-id") Long userId,
            @PathVariable("report-id") Long reportId) {
        ReportDto report = reportService.getReportById(userId, reportId);
        return ResponseEntity.ok(ApiResponse.success(report, "Report fetched successfully"));
    }

    @PutMapping("/{report-id}")
    public ResponseEntity<ApiResponse<Object>> update(@CookieValue(name = "user-id") Long userId,
            @PathVariable("report-id") Long reportId,
            @RequestBody ReportDto reportDto) {
        ReportDto updatedReport = reportService.updateReport(userId, reportId, reportDto);
        return ResponseEntity.ok(ApiResponse.success(updatedReport, "Report updated successfully"));
    }

    @DeleteMapping("/{report-id}")
    public ResponseEntity<ApiResponse<Object>> delete(@CookieValue(name = "user-id") Long userId,
            @PathVariable("report-id") Long reportId) {
        reportService.deleteReport(userId, reportId);
        return ResponseEntity.ok(ApiResponse.success(null, "Report deleted successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@CookieValue(name = "user-id") Long userId,
            @RequestBody ReportDto reportDto) {
        ReportDto createdReport = reportService.createReport(userId, reportDto);
        return ResponseEntity.ok(ApiResponse.success(createdReport, "Report created successfully"));
    }

    @PostMapping("/{report-id}/charts/{chart-id}")
    public ResponseEntity<ApiResponse<Object>> addChartToReport(@CookieValue(name = "user-id") Long userId,
            @PathVariable("report-id") Long reportId,
            @PathVariable("chart-id") Long chartId) {
        reportService.addChartToReport(userId, reportId, chartId);
        return ResponseEntity.ok(ApiResponse.success(null, "Chart added to report successfully"));
    }

}
