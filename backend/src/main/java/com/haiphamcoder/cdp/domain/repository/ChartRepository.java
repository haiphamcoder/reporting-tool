package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Chart;

public interface ChartRepository {
    
    Optional<Chart> getChartById(Long id);

    Long getTotalChartByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getChartCountByLast30Days(Long userId);
}
