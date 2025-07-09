package com.haiphamcoder.reporting.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.haiphamcoder.reporting.domain.entity.Chart;

public interface ChartRepository {

    Page<Chart> getAllChartsByUserId(Long userId, Integer page, Integer limit);
    
    Page<Chart> getAllChartsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page, Integer limit);
    
    default Page<Chart> getAllChartsByUserId(Long userId, String search, Integer page, Integer limit) {
        return getAllChartsByUserIdAndIsDeleted(userId, false, search, page, limit);
    }

    Page<Chart> getAllChartsByUserIdOrChartId(Long userId, Set<Long> chartIds, String search, Integer page, Integer limit);
    
    Optional<Chart> getChartById(Long id);

    Long getTotalChartByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Long getTotalChartByUserIdOrChartIdAndIsDeleted(Long userId, Set<Long> chartIds, Boolean isDeleted);

    Long getChartCountByUserIdOrChartIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> chartIds, Boolean isDeleted,
            LocalDate date);

    Optional<Chart> updateChart(Chart chart);

    Chart save(Chart chart);
    
}
