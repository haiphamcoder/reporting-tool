package com.haiphamcoder.reporting.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.ReportPermission;
import com.haiphamcoder.reporting.domain.model.ReportPermissionComposeKey;
import com.haiphamcoder.reporting.repository.ReportPermissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface ReportPermissionJpaRepository extends JpaRepository<ReportPermission, ReportPermissionComposeKey> {
    Optional<ReportPermission> findByReportIdAndUserId(Long reportId, Long userId);

    List<ReportPermission> findAllByUserId(Long userId);

    void deleteAllByReportIdAndUserId(Long reportId, Long userId);

    void deleteAllByReportId(Long reportId);

    List<ReportPermission> findAllByReportId(Long reportId);
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

    @Override
    @Transactional
    public Optional<ReportPermission> saveReportPermission(ReportPermission reportPermission) {
        return Optional.of(reportPermissionJpaRepository.save(reportPermission));
    }

    @Override
    public List<ReportPermission> getAllReportPermissionsByUserId(Long userId) {
        return reportPermissionJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<ReportPermission> getAllReportPermissionsByReportId(Long reportId) {
        return reportPermissionJpaRepository.findAllByReportId(reportId);
    }

    @Override
    @Transactional
    public void deleteAllReportPermissionsByReportIdAndUserId(Long reportId, Long userId) {
        reportPermissionJpaRepository.deleteAllByReportIdAndUserId(reportId, userId);
    }

    @Override
    @Transactional
    public void deleteAllReportPermissionsByReportId(Long reportId) {
        reportPermissionJpaRepository.deleteAllByReportId(reportId);
    }
}
