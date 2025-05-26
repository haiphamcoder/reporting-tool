package com.example.reporting.controller;

import com.example.reporting.dto.ChartRequest;
import com.example.reporting.model.Chart;
import com.example.reporting.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/charts")
public class ChartController {
    @Autowired
    private ChartService chartService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    public ResponseEntity<Chart> createChart(@RequestBody ChartRequest request) {
        Chart chart = chartService.createChart(request);
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chart> getChart(@PathVariable Long id) {
        Chart chart = chartService.getChart(id);
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<List<Map<String, Object>>> getChartData(@PathVariable Long id) {
        Chart chart = chartService.getChart(id);
        List<Map<String, Object>> data = jdbcTemplate.queryForList("SELECT * FROM " + chart.getDataTable());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<Void> refreshChartData(@PathVariable Long id) {
        chartService.refreshChartData(id);
        return ResponseEntity.ok().build();
    }
} 