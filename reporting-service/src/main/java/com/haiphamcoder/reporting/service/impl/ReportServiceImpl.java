package com.haiphamcoder.reporting.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.mapper.ReportMapper;
import com.haiphamcoder.reporting.repository.ChartReportRepository;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.ReportRepository;
import com.haiphamcoder.reporting.service.ReportService;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ChartRepository chartRepository;
    private final ChartReportRepository chartReportRepository;

    @Override
    public Pair<List<ReportDto>, Metadata> getAllReportsByUserId(Long userId, Integer page, Integer limit) {
        Page<Report> reports = reportRepository.getReportsByUserId(userId, page, limit);
        return new Pair<>(reports.stream().map(ReportMapper::toReportDto).toList(),
                Metadata.builder()
                        .totalElements(reports.getTotalElements())
                        .numberOfElements(reports.getNumberOfElements())
                        .totalPages(reports.getTotalPages())
                        .currentPage(reports.getNumber())
                        .pageSize(reports.getSize())
                        .build());
    }

    @Override
    public ReportDto getReportById(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        ReportDto reportDto = ReportMapper.toReportDto(report.get());
        List<ChartReport> chartReports = chartReportRepository.getChartReportsByReportId(reportId);
        List<Chart> charts = chartReports.stream().map(chartReport -> chartRepository.getChartById(chartReport.getChartId()).get()).collect(Collectors.toList());
        reportDto.setCharts(charts.stream().map(ChartMapper::toChartDto).collect(Collectors.toList()));
        return reportDto;
    }

    @Override
    public ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        ReportDto updatedReportDto = ReportMapper.updateReportDto(report.get(), reportDto);
        Optional<Report> updatedReport = reportRepository.updateReport(ReportMapper.toEntity(updatedReportDto));
        if (updatedReport.isEmpty()) {
            throw new RuntimeException("Update report failed");
        }
        return ReportMapper.toReportDto(updatedReport.get());
    }

    @Override
    public void deleteReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        report.get().setIsDeleted(true);
        reportRepository.updateReport(report.get());
    }

    @Override
    public ReportDto createReport(Long userId, ReportDto reportDto) {
        if (StringUtils.isNullOrEmpty(reportDto.getName())) {
            throw new InvalidInputException("name");
        }

        Report report = ReportMapper.toEntity(reportDto);
        report.setId(SnowflakeIdGenerator.getInstance().generateId());
        report.setUserId(userId);

        Optional<Report> savedReport = reportRepository.createReport(report);
        if (savedReport.isEmpty()) {
            throw new RuntimeException("Create report failed");
        }
        return ReportMapper.toReportDto(savedReport.get());
    }

    @Override
    public void addChartToReport(Long userId, Long reportId, Long chartId) {
        Optional<Report> existingReport = reportRepository.getReportById(reportId);
        if (existingReport.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        Optional<Chart> existingChart = chartRepository.getChartById(chartId);
        if (existingChart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        ChartReport chartReport = new ChartReport();
        chartReport.setReportId(reportId);
        chartReport.setChartId(chartId);
        chartReportRepository.save(chartReport);
    }

}
