package com.haiphamcoder.reporting.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.model.ChartPermissionComposeKey;
import com.haiphamcoder.reporting.repository.ChartPermissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ChartPermissionJpaRepository extends JpaRepository<ChartPermission, ChartPermissionComposeKey> {
    Optional<ChartPermission> findByChartIdAndUserId(Long chartId, Long userId);

    List<ChartPermission> findAllByUserId(Long userId);

    List<ChartPermission> findAllByChartId(Long chartId);

    void deleteAllByChartId(Long chartId);
}

@Component
@Slf4j
@RequiredArgsConstructor
public class ChartPermissionRepositoryImpl implements ChartPermissionRepository {
    private final ChartPermissionJpaRepository chartPermissionJpaRepository;

    @Override
    public Optional<ChartPermission> getChartPermissionByChartIdAndUserId(Long chartId, Long userId) {
        return chartPermissionJpaRepository.findByChartIdAndUserId(chartId, userId);
    }

    @Override
    public Optional<ChartPermission> saveChartPermission(ChartPermission chartPermission) {
        return Optional.of(chartPermissionJpaRepository.save(chartPermission));
    }

    @Override
    public List<ChartPermission> getAllChartPermissionsByUserId(Long userId) {
        return chartPermissionJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<ChartPermission> getChartPermissionsByChartId(Long chartId) {
        return chartPermissionJpaRepository.findAllByChartId(chartId);
    }

    @Override
    @Transactional
    public void deleteAllChartPermissionsByChartId(Long chartId) {
        chartPermissionJpaRepository.deleteAllByChartId(chartId);
    }
}
