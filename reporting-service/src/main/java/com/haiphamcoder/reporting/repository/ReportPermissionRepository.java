package com.haiphamcoder.reporting.repository;

import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ReportPermission;

public interface ReportPermissionRepository {
    Optional<ReportPermission> getReportPermissionByReportIdAndUserId(Long reportId, Long userId);
}
