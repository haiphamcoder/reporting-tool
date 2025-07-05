package com.haiphamcoder.reporting.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ReportPersistenceException;
import com.haiphamcoder.reporting.domain.model.request.CreateReportRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareReportRequest;
import com.haiphamcoder.reporting.domain.model.request.UpdateReportRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.mapper.ReportMapper;
import com.haiphamcoder.reporting.repository.ChartReportRepository;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.ReportPermissionRepository;
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
    private final ReportPermissionRepository reportPermissionRepository;

    @Override
    public Pair<List<ReportDto>, Metadata> getAllReportsByUserId(Long userId, String search, Integer page,
            Integer limit) {
        Page<Report> reports = reportRepository.getReportsByUserId(userId, search, page, limit);
        return new Pair<>(reports.stream().map(report -> {
            List<ChartReport> chartReports = chartReportRepository.getChartReportsByReportId(report.getId());
            List<Chart> charts = chartReports.stream()
                    .map(chartReport -> chartRepository.getChartById(chartReport.getChartId()).get()).toList();
            ReportDto reportDto = ReportMapper.toReportDto(report);
            reportDto.setCharts(charts.stream().map(ChartMapper::toChartDto).toList());
            return reportDto;
        }).toList(),
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
        List<Chart> charts = chartReports.stream()
                .map(chartReport -> chartRepository.getChartById(chartReport.getChartId()).get()).toList();
        reportDto.setCharts(charts.stream().map(ChartMapper::toChartDto).toList());
        return reportDto;
    }

    @Override
    public ReportDto updateReport(Long userId, Long reportId, UpdateReportRequest updateReportRequest) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        ReportDto updatedReportDto = ReportMapper.updateReportDto(report.get(), ReportDto.builder()
                .id(reportId.toString())
                .name(updateReportRequest.getName())
                .description(updateReportRequest.getDescription())
                .chartIds(updateReportRequest.getChartIds())
                .build());

        removeAllChartsFromReport(reportId);
        addChartsToReport(reportId, updateReportRequest.getChartIds().stream().map(Long::parseLong).toList());

        Optional<Report> updatedReport = reportRepository.updateReport(ReportMapper.toEntity(updatedReportDto));
        if (updatedReport.isEmpty()) {
            throw new ReportPersistenceException("Update report failed");
        }
        return ReportMapper.toReportDto(updatedReport.get());
    }

    private void removeAllChartsFromReport(Long reportId) {
        List<ChartReport> chartReports = chartReportRepository.getChartReportsByReportId(reportId);
        chartReports.forEach(chartReport -> chartReportRepository.deleteByChartIdAndReportId(chartReport.getChartId(),
                reportId));
    }

    private void addChartsToReport(Long reportId, List<Long> chartIds) {
        chartIds.forEach(chartId -> {
            ChartReport chartReport = new ChartReport();
            chartReport.setReportId(reportId);
            chartReport.setChartId(chartId);
            chartReportRepository.save(chartReport);
        });
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
    public ReportDto createReport(Long userId, CreateReportRequest createReportRequest) {
        if (StringUtils.isNullOrEmpty(createReportRequest.getName())) {
            throw new InvalidInputException("name");
        }

        ReportDto reportDto = ReportDto.builder()
                .name(createReportRequest.getName())
                .description(createReportRequest.getDescription())
                .build();
        Report report = ReportMapper.toEntity(reportDto);
        report.setId(SnowflakeIdGenerator.getInstance().generateId());
        report.setUserId(userId);

        Optional<Report> savedReport = reportRepository.createReport(report);
        if (savedReport.isEmpty()) {
            throw new ReportPersistenceException("Create report failed");
        }

        addChartsToReport(savedReport.get().getId(),
                createReportRequest.getChartIds().stream().map(Long::parseLong).toList());
        ReportDto savedReportDto = ReportMapper.toReportDto(savedReport.get());
        savedReportDto.setChartIds(createReportRequest.getChartIds());
        return savedReportDto;
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
        chartReport.setCreatedAt(LocalDateTime.now());
        chartReport.setModifiedAt(LocalDateTime.now());
        chartReportRepository.save(chartReport);
    }

    @Override
    public void shareReport(Long userId, Long reportId, ShareReportRequest shareReportRequest) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (report.get().getUserId() != userId) {
            throw new ForbiddenException("You are not allowed to share this report");
        }
        for (ShareReportRequest.UserReportPermission userReportPermission : shareReportRequest
                .getUserReportPermissions()) {
            Optional<ReportPermission> existingReportPermission = reportPermissionRepository
                    .getReportPermissionByReportIdAndUserId(report.get().getId(), userReportPermission.getUserId());
            if (existingReportPermission.isPresent()) {
                existingReportPermission.get().setPermission(userReportPermission.getPermission().getValue());
                reportPermissionRepository.saveReportPermission(existingReportPermission.get());
            } else {
                ReportPermission reportPermission = ReportPermission.builder()
                        .reportId(report.get().getId())
                        .userId(userReportPermission.getUserId())
                        .permission(userReportPermission.getPermission().getValue())
                        .build();
                reportPermissionRepository.saveReportPermission(reportPermission);
            }
        }
    }

    @Override
    public ReportDto cloneReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!Objects.equals(report.get().getUserId(), userId)) {
            Optional<ReportPermission> reportPermission = reportPermissionRepository
                    .getReportPermissionByReportIdAndUserId(reportId, userId);
            if (reportPermission.isEmpty()) {
                throw new ForbiddenException("You are not allowed to clone this report");
            }
            if (!reportPermission.get().hasReadPermission()) {
                throw new ForbiddenException("You are not allowed to clone this report");
            }
        }
        ReportDto clonedReport = ReportMapper.toReportDto(report.get());
        clonedReport.setId(String.valueOf(SnowflakeIdGenerator.getInstance().generateId()));
        clonedReport.setName(clonedReport.getName() + " (Copy)");
        clonedReport.setUserId(String.valueOf(userId));
        clonedReport.setCreatedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(ReportMapper.toEntity(clonedReport));
        if (savedReport == null) {
            throw new RuntimeException("Clone report failed");
        }
        return ReportMapper.toReportDto(savedReport);
    }

}
