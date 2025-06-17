package com.haiphamcoder.reporting.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.model.StatisticData;
import com.haiphamcoder.reporting.domain.model.StatisticData.StatisticItem;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.ReportRepository;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

        private final SourceRepository sourceRepository;
        private final ChartRepository chartRepository;
        private final ReportRepository reportRepository;

        @Override
        public StatisticData getStatistics(Long userId) {

                Long totalSource = sourceRepository.getTotalSourceByUserIdAndIsDeleted(userId, false);
                List<Long> sourceCountByLast30Days = sourceRepository.getSourceCountByLast30Days(userId);

                Long totalChart = chartRepository.getTotalChartByUserIdAndIsDeleted(userId, false);
                List<Long> chartCountByLast30Days = chartRepository.getChartCountByLast30Days(userId);

                Long totalReport = reportRepository.getTotalReportByUserIdAndIsDeleted(userId, false);
                List<Long> reportCountByLast30Days = reportRepository.getReportCountByLast30Days(userId);

                return StatisticData.builder()
                                .sourceStatistic(StatisticItem.builder()
                                                .total(totalSource)
                                                .data(sourceCountByLast30Days)
                                                .build())
                                .chartStatistic(StatisticItem.builder()
                                                .total(totalChart)
                                                .data(chartCountByLast30Days)
                                                .build())
                                .reportStatistic(StatisticItem.builder()
                                                .total(totalReport)
                                                .data(reportCountByLast30Days)
                                                .build())
                                .build();
        }

}
