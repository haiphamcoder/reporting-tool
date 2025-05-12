package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Source;

public interface SourceRepository {

    Optional<Source> getSourceById(Long id);

    Boolean checkSourceName(String userId, String sourceName);

    List<Source> getAllSourcesByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    default List<Source> getAllSourcesByUserId(Long userId) {
        return getAllSourcesByUserIdAndIsDeleted(userId, false);
    }

    Optional<Source> deleteSourceById(Long id);

    Optional<Source> createSource(Source source);

    Long getTotalSourceByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    List<Long> getSourceCountByLast30Days(Long userId);

    Optional<Source> updateSource(Source source);

}