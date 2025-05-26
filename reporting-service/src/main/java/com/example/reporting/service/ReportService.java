package com.example.reporting.service;

import com.example.reporting.dto.ReportRequest;
import com.example.reporting.model.Chart;
import com.example.reporting.model.Report;
import com.example.reporting.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ChartService chartService;

    @Transactional
    public Report createReport(ReportRequest request) {
        Report report = new Report();
        report.setName(request.getName());
        report.setDescription(request.getDescription());
        return reportRepository.save(report);
    }

    public Report getReport(Long id) {
        return reportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Transactional
    public Report addChartToReport(Long reportId, Long chartId) {
        Report report = getReport(reportId);
        Chart chart = chartService.getChart(chartId);
        report.getCharts().add(chart);
        return reportRepository.save(report);
    }

    @Transactional
    public Report removeChartFromReport(Long reportId, Long chartId) {
        Report report = getReport(reportId);
        Chart chart = chartService.getChart(chartId);
        report.getCharts().remove(chart);
        return reportRepository.save(report);
    }
} 