package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ReportPermission;

public interface ReportPermissionRepository {
    Optional<ReportPermission> getReportPermissionByReportIdAndUserId(Long reportId, Long userId);

    Optional<ReportPermission> saveReportPermission(ReportPermission reportPermission);

    List<ReportPermission> getAllReportPermissionsByUserId(Long userId);

    List<ReportPermission> getAllReportPermissionsByReportId(Long reportId);

    void deleteAllReportPermissionsByReportIdAndUserId(Long reportId, Long userId);

    void deleteAllReportPermissionsByReportIdAndUserIdNot(Long reportId, Long userId);
}
