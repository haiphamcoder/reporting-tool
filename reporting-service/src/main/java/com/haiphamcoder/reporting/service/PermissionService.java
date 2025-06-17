package com.haiphamcoder.reporting.service;

public interface PermissionService {

    boolean hasReadSourcePermission(Long userId, Long sourceId);

    boolean hasWriteSourcePermission(Long userId, Long sourceId);

    boolean hasExecuteSourcePermission(Long userId, Long sourceId);

    boolean hasReadReportPermission(Long userId, Long reportId);

    boolean hasWriteReportPermission(Long userId, Long reportId);

    boolean hasExecuteReportPermission(Long userId, Long reportId);

    boolean hasReadChartPermission(Long userId, Long chartId);

    boolean hasWriteChartPermission(Long userId, Long chartId);

    boolean hasExecuteChartPermission(Long userId, Long chartId);
    
}
