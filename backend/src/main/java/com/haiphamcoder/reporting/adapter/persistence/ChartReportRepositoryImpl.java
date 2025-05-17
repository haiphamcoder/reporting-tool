package com.haiphamcoder.reporting.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.model.ChartReportComposeKey;
import com.haiphamcoder.reporting.domain.repository.ChartReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ChartReportJpaRepository extends JpaRepository<ChartReport, ChartReportComposeKey> {
    Optional<ChartReport> findByChartIdAndReportId(Long chartId, Long reportId);
}

@Component
@Slf4j
@RequiredArgsConstructor
public class ChartReportRepositoryImpl implements ChartReportRepository {
    private final ChartReportJpaRepository chartReportJpaRepository;

    @Override
    public Optional<ChartReport> getChartReportByChartIdAndReportId(Long chartId, Long reportId) {
        return chartReportJpaRepository.findByChartIdAndReportId(chartId, reportId);
    }

}
