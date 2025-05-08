package com.haiphamcoder.cdp.application.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.StatisticsService;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.exception.UserNotFoundException;
import com.haiphamcoder.cdp.domain.model.StatisticData;
import com.haiphamcoder.cdp.domain.model.StatisticData.StatisticItem;
import com.haiphamcoder.cdp.domain.repository.ChartRepository;
import com.haiphamcoder.cdp.domain.repository.ReportRepository;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
import com.haiphamcoder.cdp.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;
    private final ChartRepository chartRepository;
    private final ReportRepository reportRepository;

    @Override
    public StatisticData getStatistics(Long userId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        Long totalSource = sourceRepository.getTotalSourceByUserIdAndIsDeleted(userId, false);
        List<Long> sourceCountByLast30Days = sourceRepository.getSourceCountByLast30Days(userId);
        long lastDaySourceCount = sourceCountByLast30Days.get(29);
        long secondLastDaySourceCount = sourceCountByLast30Days.get(28);
        String sourceTrend = lastDaySourceCount > secondLastDaySourceCount ? "up"
                : (lastDaySourceCount < secondLastDaySourceCount ? "down" : "stable");

        Long totalChart = chartRepository.getTotalChartByUserIdAndIsDeleted(userId, false);
        List<Long> chartCountByLast30Days = chartRepository.getChartCountByLast30Days(userId);
        long lastDayChartCount = chartCountByLast30Days.get(29);
        long secondLastDayChartCount = chartCountByLast30Days.get(28);
        String chartTrend = lastDayChartCount > secondLastDayChartCount ? "up"
                : (lastDayChartCount < secondLastDayChartCount ? "down" : "stable");

        Long totalReport = reportRepository.getTotalReportByUserIdAndIsDeleted(userId, false);
        List<Long> reportCountByLast30Days = reportRepository.getReportCountByLast30Days(userId);
        long lastDayReportCount = reportCountByLast30Days.get(29);
        long secondLastDayReportCount = reportCountByLast30Days.get(28);
        String reportTrend = lastDayReportCount > secondLastDayReportCount ? "up"
                : (lastDayReportCount < secondLastDayReportCount ? "down" : "stable");

        return StatisticData.builder()
                .sourceStatistic(StatisticItem.builder()
                        .total(totalSource)
                        .trend(sourceTrend)
                        .data(sourceCountByLast30Days)
                        .build())
                .chartStatistic(StatisticItem.builder()
                        .total(totalChart)
                        .trend(chartTrend)
                        .data(chartCountByLast30Days)
                        .build())
                .reportStatistic(StatisticItem.builder()
                        .total(totalReport)
                        .trend(reportTrend)
                        .data(reportCountByLast30Days)
                        .build())
                .build();
    }

}
