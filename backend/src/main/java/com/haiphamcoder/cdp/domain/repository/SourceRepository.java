package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Source;

public interface SourceRepository {

    Optional<Source> getSourceById(Long id);

    List<Source> getAllSourcesByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    default List<Source> getAllSourcesByUserId(Long userId) {
        return getAllSourcesByUserIdAndIsDeleted(userId, false);
    }

    Optional<Source> deleteSourceById(Long id);

    Optional<Source> createSource(Source source);

}