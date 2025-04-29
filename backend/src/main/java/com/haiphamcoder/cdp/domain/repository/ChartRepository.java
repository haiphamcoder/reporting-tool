package com.haiphamcoder.cdp.domain.repository;

import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Chart;

public interface ChartRepository {
    
    Optional<Chart> getChartById(Long id);
}
