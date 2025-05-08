package com.haiphamcoder.cdp.adapter.persistence;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Report;
import com.haiphamcoder.cdp.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface ReportJpaRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.user.id = :userId AND DATE(r.createdAt) = DATE(:date)")
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
    public List<Report> getReportsByUserId(Long userId) {
        return reportJpaRepository.findAllByUserIdAndIsDeleted(userId, false);
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

        Collections.reverse(result);
        return result;
    }
    
}
