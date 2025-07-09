package com.haiphamcoder.reporting.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.haiphamcoder.reporting.domain.entity.Source;

public interface SourceRepository {

    Optional<Source> getSourceById(Long id);

    boolean checkSourceName(Long userId, String sourceName);

    Page<Source> getAllSourcesByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page,
            Integer limit);

    default Page<Source> getAllSourcesByUserId(Long userId, String search, Integer page, Integer limit) {
        return getAllSourcesByUserIdAndIsDeleted(userId, false, search, page, limit);
    }

    Page<Source> getAllSourcesByUserIdOrSourceId(Long userId, Set<Long> sourceIds, String search, Integer page,
            Integer limit);

    Optional<Source> deleteSourceById(Long id);

    Optional<Source> createSource(Source source);

    Long getTotalSourceByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Long getTotalSourceByUserIdOrSourceIdAndIsDeleted(Long userId, Set<Long> sourceIds, Boolean isDeleted);

    Long getSourceCountByUserIdOrSourceIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> sourceIds, Boolean isDeleted,
            LocalDate date);

    Optional<Source> updateSource(Source source);

}