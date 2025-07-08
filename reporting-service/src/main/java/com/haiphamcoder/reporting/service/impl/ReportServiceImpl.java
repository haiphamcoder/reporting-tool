package com.haiphamcoder.reporting.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ReportDto;
import com.haiphamcoder.reporting.domain.dto.ReportDto.UserReportPermission;
import com.haiphamcoder.reporting.domain.dto.ReportDto.ReportConfig.Block;
import com.haiphamcoder.reporting.domain.dto.ReportDto.ReportConfig.Block.BlockType;
import com.haiphamcoder.reporting.domain.dto.UserDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.enums.ChartPermissionType;
import com.haiphamcoder.reporting.domain.enums.ReportPermissionType;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ReportPersistenceException;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import com.haiphamcoder.reporting.domain.model.QueryOption.Join;
import com.haiphamcoder.reporting.domain.model.request.CreateReportRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareReportRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ChartMapper;
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
            reportDto.setCanEdit(permissionService.hasEditReportPermission(userId, report.getId()));
            reportDto.setCanShare(permissionService.hasOwnerReportPermission(userId, report.getId()));
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
        if (!permissionService.hasViewReportPermission(userId, reportId)) {
            throw new ForbiddenException("You are not allowed to get this report");
        }
        ReportDto reportDto = ReportMapper.toReportDto(report.get());
        reportDto.setCanEdit(permissionService.hasEditReportPermission(userId, reportId));
        reportDto.setCanShare(permissionService.hasOwnerReportPermission(userId, reportId));
        return reportDto;
    }

    @Override
    public ReportDto updateReport(Long userId, Long reportId, ReportDto reportDto) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!report.get().getUserId().equals(userId) && !permissionService.hasEditReportPermission(userId, reportId)) {
            throw new ForbiddenException("You are not allowed to update this report");
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
        if (!Objects.equals(report.get().getUserId(), userId)) {
            if (permissionService.hasViewReportPermission(userId, reportId)) {
                reportPermissionRepository.deleteAllReportPermissionsByReportIdAndUserId(reportId, userId);
            } else {
                throw new ForbiddenException("You are not allowed to delete this chart");
            }
        } else {
            report.get().setIsDeleted(true);
            reportRepository.updateReport(report.get());
            reportPermissionRepository.deleteAllReportPermissionsByReportIdAndUserIdNot(reportId, userId);
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
        ReportPermission reportPermission = ReportPermission.builder()
                .reportId(savedReport.get().getId())
                .userId(userId)
                .permission(ReportPermissionType.OWNER.getValue())
                .build();
        reportPermissionRepository.saveReportPermission(reportPermission);
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
        if (!permissionService.hasEditReportPermission(userId, reportId)
                || !permissionService.hasViewChartPermission(userId, chartId)) {
            throw new ForbiddenException("You are not allowed to add this chart to report");
        }
        ChartReport chartReport = new ChartReport();
        chartReport.setReportId(reportId);
        chartReport.setChartId(chartId);
        chartReport.setCreatedAt(LocalDateTime.now());
        chartReport.setModifiedAt(LocalDateTime.now());
        chartReportRepository.save(chartReport);
    }

    public UserReportPermission getUserReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isEmpty()) {
            throw new ResourceNotFoundException("Not found report permission", userId);
        }
        UserDto userDto = userGrpcClient.getUserById(reportPermission.get().getUserId());
        return UserReportPermission.builder()
                .userId(String.valueOf(reportPermission.get().getUserId()))
                .name(userDto.getFirstName() + " " + userDto.getLastName())
                .email(userDto.getEmail())
                .avatar(userDto.getAvatarUrl())
                .permission(ReportPermissionType.fromValue(reportPermission.get().getPermission()))
                .build();
    }

    @Override
    public List<UserReportPermission> getShareReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (Objects.equals(report.get().getUserId(), userId)) {
            List<ReportPermission> reportPermissions = reportPermissionRepository
                    .getAllReportPermissionsByReportId(reportId);
            List<UserReportPermission> userReportPermissions = new ArrayList<>();
            for (ReportPermission reportPermission : reportPermissions) {
                if (reportPermission.getPermission().equals(ReportPermissionType.OWNER.getValue())) {
                    continue;
                }
                UserDto userDto = userGrpcClient.getUserById(reportPermission.getUserId());
                userReportPermissions.add(UserReportPermission.builder()
                        .userId(String.valueOf(reportPermission.getUserId()))
                        .name(userDto.getFirstName() + " " + userDto.getLastName())
                        .email(userDto.getEmail())
                        .avatar(userDto.getAvatarUrl())
                        .permission(ReportPermissionType.fromValue(reportPermission.getPermission()))
                        .build());
            }
            return userReportPermissions;
        } else {
            throw new ForbiddenException("You are not allowed to get share chart");
        }
    }

    @Override
    public void updateShareReport(Long userId, Long reportId, ShareReportRequest shareReportRequest) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (Objects.equals(report.get().getUserId(), userId)) {
            reportPermissionRepository.deleteAllReportPermissionsByReportIdAndUserIdNot(reportId, userId);
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
                updateViewChartPermissionRelationToReport(Long.parseLong(userReportPermission.getUserId()), reportId);
            }
        } else {
            throw new ForbiddenException("You are not allowed to share this report");
        }
    }

    private void updateViewChartPermissionRelationToReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        ReportDto reportDto = ReportMapper.toReportDto(report.get());
        Set<String> relatedChartIds = new HashSet<>();
        List<Block> blocks = reportDto.getConfig().getBlocks();
        for (Block block : blocks) {
            if (block.getType() == BlockType.CHART && block.getContent() != null
                    && block.getContent().getChartId() != null
                    && !StringUtils.isNullOrEmpty(block.getContent().getChartId())) {
                relatedChartIds.add(block.getContent().getChartId());
            }

        }
        for (String chartId : relatedChartIds) {
            if (!permissionService.hasViewChartPermission(userId, Long.parseLong(chartId))) {
                ChartPermission chartPermission = ChartPermission.builder()
                        .chartId(Long.parseLong(chartId))
                        .userId(userId)
                        .permission(ReportPermissionType.VIEW.getValue())
                        .build();
                chartPermissionRepository.saveChartPermission(chartPermission);
                updateViewSourcePermissionRelationToChart(userId, Long.parseLong(chartId));
            }
        }
    }

    private void updateViewSourcePermissionRelationToChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        ChartDto chartDto = ChartMapper.toChartDto(chart.get());
        Set<String> relatedSourceIds = new HashSet<>();
        QueryOption queryOption = chartDto.getConfig().getQueryOption();
        relatedSourceIds.add(queryOption.getTable());
        List<Join> joins = queryOption.getJoins();
        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                if (StringUtils.isNullOrEmpty(join.getTable())) {
                    continue;
                }
                relatedSourceIds.add(join.getTable());
            }
        }
        for (String sourceId : relatedSourceIds) {
            if (!permissionService.hasViewSourcePermission(userId, Long.parseLong(sourceId))) {
                SourcePermission newSourcePermission = SourcePermission.builder()
                        .sourceId(Long.parseLong(sourceId))
                        .userId(userId)
                        .permission(ChartPermissionType.VIEW.getValue())
                        .build();
                sourcePermissionRepository.saveSourcePermission(newSourcePermission);
            }
        }

    }

    @Override
    public ReportDto cloneReport(Long userId, Long reportId) {
        Optional<Report> report = reportRepository.getReportById(reportId);
        if (report.isEmpty()) {
            throw new ResourceNotFoundException("Report", reportId);
        }
        if (!Objects.equals(report.get().getUserId(), userId)
                && !permissionService.hasViewReportPermission(userId, reportId)) {
            throw new ForbiddenException("You are not allowed to clone this report");
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
        ReportPermission reportPermission = ReportPermission.builder()
                .reportId(savedReport.getId())
                .userId(userId)
                .permission(ReportPermissionType.OWNER.getValue())
                .build();
        reportPermissionRepository.saveReportPermission(reportPermission);
        return ReportMapper.toReportDto(savedReport);
    }

}
