package com.haiphamcoder.reporting.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.model.ChartPermissionComposeKey;
import com.haiphamcoder.reporting.domain.repository.ChartPermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ChartPermissionJpaRepository extends JpaRepository<ChartPermission, ChartPermissionComposeKey> {
    Optional<ChartPermission> findByChartIdAndUserId(Long chartId, Long userId);
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
}
