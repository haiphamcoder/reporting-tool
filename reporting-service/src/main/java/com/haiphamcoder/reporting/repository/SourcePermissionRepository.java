package com.haiphamcoder.reporting.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.SourcePermission;

public interface SourcePermissionRepository {

    Optional<SourcePermission> getSourcePermissionBySourceIdAndUserId(Long sourceId, Long userId);

    List<SourcePermission> getSourcePermissionsBySourceId(Long sourceId);

    SourcePermission createSourcePermission(SourcePermission sourcePermission);

    SourcePermission saveSourcePermission(SourcePermission sourcePermission);

    List<SourcePermission> getAllSourcePermissionsByUserId(Long userId);
    
}
