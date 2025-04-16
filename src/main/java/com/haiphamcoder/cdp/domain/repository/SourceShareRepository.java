package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.SourceShare;

public interface SourceShareRepository {

    Optional<SourceShare> getSourceShareBySourceIdAndUserId(Long sourceId, Long userId);

    List<SourceShare> getSourceSharesBySourceId(Long sourceId);
    
}
