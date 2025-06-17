package com.haiphamcoder.reporting.service;

import com.haiphamcoder.reporting.domain.model.StatisticData;

public interface StatisticsService {

    StatisticData getStatistics(Long userId);

}
