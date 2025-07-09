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

import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ReportJpaRepository extends JpaRepository<Report, Long> {
    Page<Report> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted, Pageable pageable);

    @Query("SELECT r FROM Report r WHERE r.userId = :userId AND r.isDeleted = :isDeleted AND (r.name LIKE %:search% OR r.description LIKE %:search%)")
    Page<Report> findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Report r WHERE (r.userId = :userId OR r.id IN :reportIds) AND r.isDeleted = :isDeleted")
    Long countByUserIdOrReportIdAndIsDeleted(@Param("userId") Long userId, @Param("reportIds") Set<Long> reportIds,
            @Param("isDeleted") Boolean isDeleted);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(r) FROM Report r WHERE (r.userId = :userId OR r.id IN :reportIds) AND r.isDeleted = :isDeleted AND DATE(r.createdAt) = DATE(:date)")
    Long countByUserIdOrReportIdAndIsDeletedAndCreatedDate(@Param("userId") Long userId,
            @Param("reportIds") Set<Long> reportIds, @Param("isDeleted") Boolean isDeleted, @Param("date") LocalDate date);

    @Query("SELECT r FROM Report r WHERE (r.userId = :userId OR r.id IN :reportIds) AND r.isDeleted = false AND (r.name LIKE %:search% OR r.description LIKE %:search%)")
    Page<Report> findAllByUserIdOrReportId(@Param("userId") Long userId, @Param("reportIds") Set<Long> reportIds,
            @Param("search") String search, Pageable pageable);
}

@Component
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository reportJpaRepository;

    @Override
    public Optional<Report> getReportById(Long id) {
        return reportJpaRepository.findById(id);
    }

    @Override
    public Page<Report> getReportsByUserId(Long userId, Integer page, Integer limit) {
        return reportJpaRepository.findAllByUserIdAndIsDeleted(userId, false, PageRequest.of(page, limit));
    }

    @Override
    public Page<Report> getReportsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page,
            Integer limit) {
        return reportJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId, isDeleted,
                search, PageRequest.of(page, limit));
    }

    @Override
    public Page<Report> getReportsByUserIdOrReportId(Long userId, Set<Long> reportIds, String search, Integer page,
            Integer limit) {
        return reportJpaRepository.findAllByUserIdOrReportId(userId, reportIds, search, PageRequest.of(page, limit));
    }

    @Override
    public Long getTotalReportByUserIdAndIsDeleted(Long userId, Boolean isDeleted) {
        return reportJpaRepository.countByUserIdAndIsDeleted(userId, isDeleted);
    }

    @Override
    public Optional<Report> updateReport(Report report) {
        return Optional.of(reportJpaRepository.save(report));
    }

    @Override
    public Optional<Report> createReport(Report report) {
        return Optional.of(reportJpaRepository.save(report));
    }

    @Override
    public Report save(Report report) {
        return reportJpaRepository.save(report);
    }

    @Override
    public Long getTotalReportByUserIdOrReportIdAndIsDeleted(Long userId, Set<Long> reportIds, Boolean isDeleted) {
        return reportJpaRepository.countByUserIdOrReportIdAndIsDeleted(userId, reportIds, isDeleted);
    }

    @Override
    public Long getReportCountByUserIdOrReportIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> reportIds,
            Boolean isDeleted, LocalDate date) {
        return reportJpaRepository.countByUserIdOrReportIdAndIsDeletedAndCreatedDate(userId, reportIds, isDeleted,
                date);
    }

}
