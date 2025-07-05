package com.haiphamcoder.reporting.service;

public interface PermissionService {

    boolean hasViewSourcePermission(Long userId, Long sourceId);

    boolean hasEditSourcePermission(Long userId, Long sourceId);

    boolean hasViewReportPermission(Long userId, Long reportId);

    boolean hasEditReportPermission(Long userId, Long reportId);

    boolean hasViewChartPermission(Long userId, Long chartId);

    boolean hasEditChartPermission(Long userId, Long chartId);

}
