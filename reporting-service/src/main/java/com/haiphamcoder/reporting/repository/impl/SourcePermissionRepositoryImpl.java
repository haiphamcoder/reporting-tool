package com.haiphamcoder.reporting.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.model.SourcePermissionComposeKey;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface SourcePermissionJpaRepository extends JpaRepository<SourcePermission, SourcePermissionComposeKey> {
    Optional<SourcePermission> findBySourceIdAndUserId(Long sourceId, Long userId);

    List<SourcePermission> findAllBySourceId(Long sourceId);

    List<SourcePermission> findAllByUserId(Long userId);
}

@Component
@RequiredArgsConstructor
public class SourcePermissionRepositoryImpl implements SourcePermissionRepository {

    private final SourcePermissionJpaRepository sourcePermissionJpaRepository;

    @Override
    public Optional<SourcePermission> getSourcePermissionBySourceIdAndUserId(Long sourceId, Long userId) {
        return sourcePermissionJpaRepository.findBySourceIdAndUserId(sourceId, userId);
    }

    @Override
    public List<SourcePermission> getSourcePermissionsBySourceId(Long sourceId) {
        return sourcePermissionJpaRepository.findAllBySourceId(sourceId);
    }

    @Override
    public SourcePermission createSourcePermission(SourcePermission sourcePermission) {
        return sourcePermissionJpaRepository.save(sourcePermission);
    }

    @Override
    public SourcePermission saveSourcePermission(SourcePermission sourcePermission) {
        return sourcePermissionJpaRepository.save(sourcePermission);
    }

    @Override
    public List<SourcePermission> getAllSourcePermissionsByUserId(Long userId) {
        return sourcePermissionJpaRepository.findAllByUserId(userId);
    }

}
