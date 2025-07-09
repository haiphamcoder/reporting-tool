package com.haiphamcoder.reporting.repository.impl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    Page<Chart> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted, Pageable pageable);

    @Query("SELECT c FROM Chart c WHERE c.userId = :userId AND c.isDeleted = :isDeleted AND (c.name LIKE %:search% OR c.description LIKE %:search%)")
    Page<Chart> findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Chart c WHERE (c.userId = :userId OR c.id IN :chartIds) AND c.isDeleted = :isDeleted")
    Long countByUserIdOrChartIdAndIsDeleted(@Param("userId") Long userId, @Param("chartIds") Set<Long> chartIds,
            @Param("isDeleted") Boolean isDeleted);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(c) FROM Chart c WHERE (c.userId = :userId OR c.id IN :chartIds) AND c.isDeleted = :isDeleted AND DATE(c.createdAt) = DATE(:date)")
    Long countByUserIdOrChartIdAndIsDeletedAndCreatedDate(@Param("userId") Long userId,
            @Param("chartIds") Set<Long> chartIds, @Param("isDeleted") Boolean isDeleted, @Param("date") LocalDate date);

    @Query("SELECT c FROM Chart c WHERE (c.userId = :userId OR c.id IN :chartIds) AND c.isDeleted = false AND (c.name LIKE %:search% OR c.description LIKE %:search%)")
    Page<Chart> findAllByUserIdOrChartId(@Param("userId") Long userId, @Param("chartIds") Set<Long> chartIds,
            @Param("search") String search, Pageable pageable);
}

@Component
@RequiredArgsConstructor
public class ChartRepositoryImpl implements ChartRepository {
    private final ChartJpaRepository chartJpaRepository;

    @Override
    public Page<Chart> getAllChartsByUserId(Long userId, Integer page, Integer limit) {
        return chartJpaRepository.findAllByUserIdAndIsDeleted(userId, false, PageRequest.of(page, limit));
    }

    @Override
    public Page<Chart> getAllChartsByUserIdOrChartId(Long userId, Set<Long> chartIds, String search, Integer page,
            Integer limit) {
        return chartJpaRepository.findAllByUserIdOrChartId(userId, chartIds, search, PageRequest.of(page, limit));
    }

    @Override
    public Page<Chart> getAllChartsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page,
            Integer limit) {
        return chartJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId, isDeleted,
                search, PageRequest.of(page, limit));
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
    public Optional<Chart> updateChart(Chart chart) {
        return Optional.of(chartJpaRepository.save(chart));
    }

    @Override
    public Chart save(Chart chart) {
        return chartJpaRepository.save(chart);
    }

    @Override
    public Long getTotalChartByUserIdOrChartIdAndIsDeleted(Long userId, Set<Long> chartIds, Boolean isDeleted) {
        return chartJpaRepository.countByUserIdOrChartIdAndIsDeleted(userId, chartIds, isDeleted);
    }

    @Override
    public Long getChartCountByUserIdOrChartIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> chartIds,
            Boolean isDeleted, LocalDate date) {
        return chartJpaRepository.countByUserIdOrChartIdAndIsDeletedAndCreatedDate(userId, chartIds, isDeleted, date);
    }

}
