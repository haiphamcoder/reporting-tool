package com.haiphamcoder.reporting.repository.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(c) FROM Chart c WHERE c.userId = :userId AND DATE(c.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);
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
    public Page<Chart> getAllChartsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page, Integer limit) {
        String normalizedSearch = (search != null && search.trim().isEmpty()) ? null : search;
        
        if (normalizedSearch == null) {
            return chartJpaRepository.findAllByUserIdAndIsDeleted(userId, isDeleted, PageRequest.of(page, limit));
        } else {
            return chartJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId, isDeleted,
                    normalizedSearch, PageRequest.of(page, limit));
        }
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

        return result;
    }

    @Override
    public Optional<Chart> updateChart(Chart chart) {
        return Optional.of(chartJpaRepository.save(chart));
    }

    @Override
    public Chart save(Chart chart) {
        return chartJpaRepository.save(chart);
    }

}
