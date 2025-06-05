package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.Chart;

public interface ChartRepository {

    List<Chart> getAllChartsByUserId(Long userId);
    
    Optional<Chart> getChartById(Long id);

    Long getTotalChartByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getChartCountByLast30Days(Long userId);

    Optional<Chart> updateChart(Chart chart);

    Chart save(Chart chart);
    
}
