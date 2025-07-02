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

import com.haiphamcoder.reporting.domain.entity.Report;
import com.haiphamcoder.reporting.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ReportJpaRepository extends JpaRepository<Report, Long> {
    Page<Report> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted, Pageable pageable);

    @Query("SELECT r FROM Report r WHERE r.userId = :userId AND r.isDeleted = :isDeleted AND (r.name LIKE %:search% OR r.description LIKE %:search%)")
    Page<Report> findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, @Param("search") String search, Pageable pageable);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.userId = :userId AND DATE(r.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);
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
    public Page<Report> getReportsByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page, Integer limit) {
        String normalizedSearch = (search != null && search.trim().isEmpty()) ? null : search;
        
        if (normalizedSearch == null) {
            return reportJpaRepository.findAllByUserIdAndIsDeleted(userId, isDeleted, PageRequest.of(page, limit));
        } else {
            return reportJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId, isDeleted,
                    normalizedSearch, PageRequest.of(page, limit));
        }
    }

    @Override
    public Long getTotalReportByUserIdAndIsDeleted(Long userId, Boolean isDeleted) {
        return reportJpaRepository.countByUserIdAndIsDeleted(userId, isDeleted);
    }

    @Override
    public List<Long> getReportCountByLast30Days(Long userId) {
        List<Long> result = new LinkedList<>();
        LocalDate today = LocalDate.now();

        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            Long count = reportJpaRepository.countByUserIdAndCreatedDate(userId, date);
            result.add(count);
        }

        return result;
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

}
