package com.haiphamcoder.reporting.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ChartReport;
import com.haiphamcoder.reporting.domain.model.ChartReportComposeKey;
import com.haiphamcoder.reporting.repository.ChartReportRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ChartReportJpaRepository extends JpaRepository<ChartReport, ChartReportComposeKey> {
    Optional<ChartReport> findByChartIdAndReportId(Long chartId, Long reportId);

    List<ChartReport> findByReportId(Long reportId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChartReport cr WHERE cr.chartId = :chartId AND cr.reportId = :reportId")
    void deleteByChartIdAndReportId(@Param("chartId") Long chartId, @Param("reportId") Long reportId);
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

    @Override
    public void save(ChartReport chartReport) {
        chartReportJpaRepository.save(chartReport);
    }

    @Override
    public List<ChartReport> getChartReportsByReportId(Long reportId) {
        return chartReportJpaRepository.findByReportId(reportId);
    }

    @Override
    @Transactional
    public void deleteByChartIdAndReportId(Long chartId, Long reportId) {
        chartReportJpaRepository.deleteByChartIdAndReportId(chartId, reportId);
    }

}
