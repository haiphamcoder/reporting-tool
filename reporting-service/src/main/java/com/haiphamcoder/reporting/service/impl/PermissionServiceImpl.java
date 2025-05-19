package com.haiphamcoder.reporting.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.repository.ChartPermissionRepository;
import com.haiphamcoder.reporting.repository.ReportPermissionRepository;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final ReportPermissionRepository reportPermissionRepository;
    private final ChartPermissionRepository chartPermissionRepository;
    private final SourcePermissionRepository sourcePermissionRepository;

    @Override
    public boolean hasReadSourcePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().hasReadPermission();
        }
        return false;
    }

    @Override
    public boolean hasWriteSourcePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().hasWritePermission();
        }
        return false;
    }

    @Override
    public boolean hasExecuteSourcePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().hasExecutePermission();
        }
        return false;
    }

    @Override
    public boolean hasReadReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isPresent()) {
            return reportPermission.get().hasReadPermission();
        }
        return false;
    }

    @Override
    public boolean hasWriteReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isPresent()) {
            return reportPermission.get().hasWritePermission();
        }
        return false;
    }

    @Override
    public boolean hasExecuteReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isPresent()) {
            return reportPermission.get().hasExecutePermission();
        }
        return false;
    }

    @Override
    public boolean hasReadChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isPresent()) {
            return chartPermission.get().hasReadPermission();
        }
        return false;
    }

    @Override
    public boolean hasWriteChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isPresent()) {
            return chartPermission.get().hasWritePermission();
        }
        return false;
    }

    @Override
    public boolean hasExecuteChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isPresent()) {
            return chartPermission.get().hasExecutePermission();
        }
        return false;
    }

}
