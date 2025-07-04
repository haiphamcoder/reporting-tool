package com.haiphamcoder.reporting.repository;

import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;

public interface ChartPermissionRepository {
    Optional<ChartPermission> getChartPermissionByChartIdAndUserId(Long chartId, Long userId);

    Optional<ChartPermission> saveChartPermission(ChartPermission chartPermission);
}
