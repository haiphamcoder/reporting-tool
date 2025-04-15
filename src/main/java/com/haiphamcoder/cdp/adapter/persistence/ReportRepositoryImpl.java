package com.haiphamcoder.cdp.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Report;
import com.haiphamcoder.cdp.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ReportJpaRepository extends JpaRepository<Report, Long> {

}

@Component
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository reportJpaRepository;

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportJpaRepository.findById(id);
    }

}
