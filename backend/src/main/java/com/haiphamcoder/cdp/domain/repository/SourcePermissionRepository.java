package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.SourcePermission;

public interface SourcePermissionRepository {

    Optional<SourcePermission> getSourcePermissionBySourceIdAndUserId(Long sourceId, Long userId);

    List<SourcePermission> getSourcePermissionsBySourceId(Long sourceId);

    SourcePermission createSourcePermission(SourcePermission sourcePermission);
    
}
