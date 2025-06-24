package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.haiphamcoder.reporting.domain.entity.Chart;

public interface ChartRepository {

    Page<Chart> getAllChartsByUserId(Long userId, Integer page, Integer limit);
    
    Page<Chart> getAllChartsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page, Integer limit);
    
    default Page<Chart> getAllChartsByUserId(Long userId, String search, Integer page, Integer limit) {
        return getAllChartsByUserIdAndIsDeleted(userId, false, search, page, limit);
    }
    
    Optional<Chart> getChartById(Long id);

    Long getTotalChartByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getChartCountByLast30Days(Long userId);

    Optional<Chart> updateChart(Chart chart);

    Chart save(Chart chart);
    
}
