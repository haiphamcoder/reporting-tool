package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;

public interface ChartPermissionRepository {
    Optional<ChartPermission> getChartPermissionByChartIdAndUserId(Long chartId, Long userId);

    Optional<ChartPermission> saveChartPermission(ChartPermission chartPermission);

    List<ChartPermission> getAllChartPermissionsByUserId(Long userId);

    List<ChartPermission> getChartPermissionsByChartId(Long chartId);

    void deleteAllChartPermissionsByChartIdAndUserIdNot(Long chartId, Long userId);

    void deleteAllChartPermissionsByChartIdAndUserId(Long chartId, Long userId);
}
