package com.haiphamcoder.reporting.service.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.model.StatisticData;
import com.haiphamcoder.reporting.domain.model.StatisticData.StatisticItem;
import com.haiphamcoder.reporting.repository.ChartPermissionRepository;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.ReportPermissionRepository;
import com.haiphamcoder.reporting.repository.ReportRepository;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

        private final SourceRepository sourceRepository;
        private final SourcePermissionRepository sourcePermissionRepository;
        private final ChartRepository chartRepository;
        private final ChartPermissionRepository chartPermissionRepository;
        private final ReportRepository reportRepository;
        private final ReportPermissionRepository reportPermissionRepository;

        @Override
        public StatisticData getStatistics(Long userId) {

                List<SourcePermission> sourcePermissions = sourcePermissionRepository
                                .getAllSourcePermissionsByUserId(userId);
                Set<Long> sourceIds = sourcePermissions.stream()
                                .map(SourcePermission::getSourceId)
                                .collect(Collectors.toSet());

                Long totalSource = sourceRepository.getTotalSourceByUserIdOrSourceIdAndIsDeleted(userId, sourceIds,
                                false);
                List<Long> sourceCountByLast30Days = new LinkedList<>();
                LocalDate today = LocalDate.now();

                for (int i = 29; i >= 0; i--) {
                        LocalDate date = today.minusDays(i);
                        Long count = sourceRepository.getSourceCountByUserIdOrSourceIdAndIsDeletedAndCreatedDate(userId,
                                        sourceIds, false, date);
                        sourceCountByLast30Days.add(count);
                }

                List<ChartPermission> chartPermissions = chartPermissionRepository.getAllChartPermissionsByUserId(userId);
                Set<Long> chartIds = chartPermissions.stream()
                                .map(ChartPermission::getChartId)
                                .collect(Collectors.toSet());

                Long totalChart = chartRepository.getTotalChartByUserIdOrChartIdAndIsDeleted(userId, chartIds, false);
                List<Long> chartCountByLast30Days = new LinkedList<>();

                for (int i = 29; i >= 0; i--) {
                        LocalDate date = today.minusDays(i);
                        Long count = chartRepository.getChartCountByUserIdOrChartIdAndIsDeletedAndCreatedDate(userId,
                                        chartIds, false, date);
                        chartCountByLast30Days.add(count);
                }

                List<ReportPermission> reportPermissions = reportPermissionRepository.getAllReportPermissionsByUserId(userId);
                Set<Long> reportIds = reportPermissions.stream()
                                .map(ReportPermission::getReportId)
                                .collect(Collectors.toSet());

                Long totalReport = reportRepository.getTotalReportByUserIdOrReportIdAndIsDeleted(userId, reportIds,
                                false);
                List<Long> reportCountByLast30Days = new LinkedList<>();
                for (int i = 29; i >= 0; i--) {
                        LocalDate date = today.minusDays(i);
                        Long count = reportRepository.getReportCountByUserIdOrReportIdAndIsDeletedAndCreatedDate(userId,
                                        reportIds, false, date);
                        reportCountByLast30Days.add(count);
                }

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
