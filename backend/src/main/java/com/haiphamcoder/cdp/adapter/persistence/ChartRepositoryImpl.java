package com.haiphamcoder.cdp.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Chart;
import com.haiphamcoder.cdp.domain.repository.ChartRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ChartJpaRepository extends JpaRepository<Chart, Long> {

}

@Component
@RequiredArgsConstructor
public class ChartRepositoryImpl implements ChartRepository {
    private final ChartJpaRepository chartJpaRepository;

    @Override
    public Optional<Chart> getChartById(Long id) {
        return chartJpaRepository.findById(id);
    }

}
