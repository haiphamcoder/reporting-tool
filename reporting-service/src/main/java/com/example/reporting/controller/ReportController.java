package com.example.reporting.controller;

import com.example.reporting.dto.ReportRequest;
import com.example.reporting.model.Report;
import com.example.reporting.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequest request) {
        Report report = reportService.createReport(request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReport(@PathVariable Long id) {
        Report report = reportService.getReport(id);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/{id}/charts/{chartId}")
    public ResponseEntity<Report> addChartToReport(
            @PathVariable Long id,
            @PathVariable Long chartId) {
        Report report = reportService.addChartToReport(id, chartId);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/{id}/charts/{chartId}")
    public ResponseEntity<Report> removeChartFromReport(
            @PathVariable Long id,
            @PathVariable Long chartId) {
        Report report = reportService.removeChartFromReport(id, chartId);
        return ResponseEntity.ok(report);
    }
} 