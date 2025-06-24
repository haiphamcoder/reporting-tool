package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

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

    Optional<Source> deleteSourceById(Long id);

    Optional<Source> createSource(Source source);

    Long getTotalSourceByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getSourceCountByLast30Days(Long userId);

    Optional<Source> updateSource(Source source);

}