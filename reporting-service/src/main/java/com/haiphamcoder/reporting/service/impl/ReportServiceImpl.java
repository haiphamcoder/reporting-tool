package com.haiphamcoder.reporting.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.dto.ReportDto.UserReportPermission;
import com.haiphamcoder.reporting.domain.dto.UserDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.enums.ReportPermissionType;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ReportPersistenceException;
import com.haiphamcoder.reporting.domain.model.request.CreateReportRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareReportRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ReportMapper;
import com.haiphamcoder.reporting.repository.ChartPermissionRepository;
import com.haiphamcoder.reporting.repository.ChartReportRepository;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.ReportPermissionRepository;
import com.haiphamcoder.reporting.repository.ReportRepository;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.service.PermissionService;
import com.haiphamcoder.reporting.service.ReportService;
import com.haiphamcoder.reporting.service.UserGrpcClient;
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
    private final ChartPermissionRepository chartPermissionRepository;
    private final SourcePermissionRepository sourcePermissionRepository;
    private final UserGrpcClient userGrpcClient;
    private final PermissionService permissionService;

    @Override
    public Pair<List<ReportDto>, Metadata> getAllReportsByUserId(Long userId, String search, Integer page,
            Integer limit) {
        List<ReportPermission> reportPermissions = reportPermissionRepository.getAllReportPermissionsByUserId(userId);
        Set<Long> reportIds = reportPermissions.stream().map(ReportPermission::getReportId).collect(Collectors.toSet());
        Page<Report> reports = reportRepository.getReportsByUserIdOrReportId(userId, reportIds, search, page, limit);
        return new Pair<>(reports.stream().map(report -> {
            ReportDto reportDto = ReportMapper.toReportDto(report);
            UserDto userDto = userGrpcClient.getUserById(report.getUserId());
            reportDto.setOwner(ReportDto.Owner.builder()
                    .id(String.valueOf(userDto.getId()))
                    .name(userDto.getFirstName() + " " + userDto.getLastName())
                    .email(userDto.getEmail())
                    .avatar(userDto.getAvatarUrl())
                    .build());
            reportDto.setCanEdit(report.getUserId().equals(userId)
                    || permissionService.hasEditReportPermission(userId, report.getId()));
            reportDto.setCanShare(report.getUserId().equals(userId)
                    || permissionService.hasViewReportPermission(userId, report.getId()));
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
        return ReportMapper.toReportDto(report.get());
    }

    @Override
    public ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!report.get().getUserId().equals(userId)) {
            if (!permissionService.hasEditReportPermission(userId, reportId)) {
                throw new ForbiddenException("You are not allowed to update this report");
            }
        }
        ReportDto updatedReportDto = ReportMapper.updateReportDto(report.get(), reportDto);

        Optional<Report> updatedReport = reportRepository.updateReport(ReportMapper.toEntity(updatedReportDto));
        if (updatedReport.isEmpty()) {
            throw new ReportPersistenceException("Update report failed");
        }
        return ReportMapper.toReportDto(updatedReport.get());
    }

    @Override
    public void deleteReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (report.get().getUserId().equals(userId)) {
            report.get().setIsDeleted(true);
            reportRepository.updateReport(report.get());
            reportPermissionRepository.deleteAllReportPermissionsByReportId(reportId);
        } else {
            if (!permissionService.hasEditReportPermission(userId, reportId)
                    || !permissionService.hasViewReportPermission(userId, reportId)) {
                reportPermissionRepository.deleteAllReportPermissionsByReportIdAndUserId(reportId, userId);
            }
        }
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
        chartReport.setCreatedAt(LocalDateTime.now());
        chartReport.setModifiedAt(LocalDateTime.now());
        chartReportRepository.save(chartReport);
    }

    @Override
    public List<UserReportPermission> getShareReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!Objects.equals(report.get().getUserId(), userId)) {
            throw new ForbiddenException("You are not allowed to get share report");
        }
        List<ReportPermission> reportPermissions = reportPermissionRepository
                .getAllReportPermissionsByReportId(reportId);
        return reportPermissions.stream().map(reportPermission -> {
            UserDto userDto = userGrpcClient.getUserById(reportPermission.getUserId());
            return UserReportPermission.builder()
                    .userId(String.valueOf(reportPermission.getUserId()))
                    .name(userDto.getFirstName() + " " + userDto.getLastName())
                    .email(userDto.getEmail())
                    .avatar(userDto.getAvatarUrl())
                    .permission(ReportPermissionType.fromValue(reportPermission.getPermission()))
                    .build();
        }).toList();
    }

    @Override
    public void updateShareReport(Long userId, Long reportId, ShareReportRequest shareReportRequest) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!Objects.equals(report.get().getUserId(), userId)) {
            throw new ForbiddenException("You are not allowed to share this report");
        }
        reportPermissionRepository.deleteAllReportPermissionsByReportId(reportId);
        for (UserReportPermission userReportPermission : shareReportRequest.getUserReportPermissions()) {
            if (String.valueOf(userId).equals(userReportPermission.getUserId())) {
                continue;
            }
            ReportPermission reportPermission = ReportPermission.builder()
                    .reportId(report.get().getId())
                    .userId(Long.parseLong(userReportPermission.getUserId()))
                    .permission(userReportPermission.getPermission().getValue())
                    .build();
            reportPermissionRepository.saveReportPermission(reportPermission);
        }
    }

    @Override
    public ReportDto cloneReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!Objects.equals(report.get().getUserId(), userId)) {
            if (!permissionService.hasViewReportPermission(userId, reportId)) {
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
        List<ChartReport> chartReports = chartReportRepository.getChartReportsByReportId(reportId);
        chartReports.forEach(chartReport -> {
            ChartReport clonedChartReport = new ChartReport();
            clonedChartReport.setReportId(savedReport.getId());
            clonedChartReport.setChartId(chartReport.getChartId());
            chartReportRepository.save(clonedChartReport);
        });
        return ReportMapper.toReportDto(savedReport);
    }

}
