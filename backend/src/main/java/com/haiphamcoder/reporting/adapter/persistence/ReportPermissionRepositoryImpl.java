package com.haiphamcoder.reporting.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.model.ReportPermissionComposeKey;
import com.haiphamcoder.reporting.domain.repository.ReportPermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ReportPermissionJpaRepository extends JpaRepository<ReportPermission, ReportPermissionComposeKey> {
    Optional<ReportPermission> findByReportIdAndUserId(Long reportId, Long userId);
}

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportPermissionRepositoryImpl implements ReportPermissionRepository {
    private final ReportPermissionJpaRepository reportPermissionJpaRepository;

    @Override
    public Optional<ReportPermission> getReportPermissionByReportIdAndUserId(Long reportId, Long userId) {
        return reportPermissionJpaRepository.findByReportIdAndUserId(reportId, userId);
    }
}
