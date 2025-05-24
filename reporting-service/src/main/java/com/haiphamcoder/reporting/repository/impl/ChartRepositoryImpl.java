package com.haiphamcoder.reporting.repository.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.repository.ChartRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ChartJpaRepository extends JpaRepository<Chart, Long> {
    List<Chart> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(c) FROM Chart c WHERE c.userId = :userId AND DATE(c.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}

@Component
@RequiredArgsConstructor
public class ChartRepositoryImpl implements ChartRepository {
    private final ChartJpaRepository chartJpaRepository;

    @Override
    public List<Chart> getAllChartsByUserId(Long userId) {
        return chartJpaRepository.findAllByUserIdAndIsDeleted(userId, false);
    }

    @Override
    public Optional<Chart> getChartById(Long id) {
        return chartJpaRepository.findById(id);
    }

    @Override
    public Long getTotalChartByUserIdAndIsDeleted(Long userId, Boolean isDeleted) {
        return chartJpaRepository.countByUserIdAndIsDeleted(userId, isDeleted);
    }

    @Override
    public List<Long> getChartCountByLast30Days(Long userId) {
        List<Long> result = new LinkedList<>();
        LocalDate today = LocalDate.now();

        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            Long count = chartJpaRepository.countByUserIdAndCreatedDate(userId, date);
            result.add(count);
        }

        Collections.reverse(result);
        return result;
    }

    @Override
    public Optional<Chart> updateChart(Chart chart) {
        return Optional.of(chartJpaRepository.save(chart));
    }

}
