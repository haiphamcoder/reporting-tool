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
    public boolean hasViewSourcePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().hasViewPermission() || sourcePermission.get().hasEditPermission();
        }
        return false;
    }

    @Override
    public boolean hasEditSourcePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().hasEditPermission();
        }
        return false;
    }

    @Override
    public boolean hasViewReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isPresent()) {
            return reportPermission.get().hasViewPermission() || reportPermission.get().hasEditPermission();
        }
        return false;
    }

    @Override
    public boolean hasEditReportPermission(Long userId, Long reportId) {
        Optional<ReportPermission> reportPermission = reportPermissionRepository
                .getReportPermissionByReportIdAndUserId(reportId, userId);
        if (reportPermission.isPresent()) {
            return reportPermission.get().hasEditPermission();
        }
        return false;
    }

    @Override
    public boolean hasViewChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isPresent()) {
            return chartPermission.get().hasViewPermission() || chartPermission.get().hasEditPermission();
        }
        return false;
    }

    @Override
    public boolean hasEditChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isPresent()) {
            return chartPermission.get().hasEditPermission();
        }
        return false;
    }

}
