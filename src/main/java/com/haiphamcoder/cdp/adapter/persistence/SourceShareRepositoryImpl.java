package com.haiphamcoder.cdp.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.SourceShare;
import com.haiphamcoder.cdp.domain.model.SourceShareComposeKey;
import com.haiphamcoder.cdp.domain.repository.SourceShareRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface SourceShareJpaRepository extends JpaRepository<SourceShare, SourceShareComposeKey> {
    Optional<SourceShare> findBySourceIdAndUserId(Long sourceId, Long userId);

    List<SourceShare> findAllBySourceId(Long sourceId);
}

@Component
@RequiredArgsConstructor
public class SourceShareRepositoryImpl implements SourceShareRepository {

    private final SourceShareJpaRepository sourceShareJpaRepository;

    @Override
    public Optional<SourceShare> getSourceShareBySourceIdAndUserId(Long sourceId, Long userId) {
        return sourceShareJpaRepository.findBySourceIdAndUserId(sourceId, userId);
    }

    @Override
    public List<SourceShare> getSourceSharesBySourceId(Long sourceId) {
        return sourceShareJpaRepository.findAllBySourceId(sourceId);
    }
    
}
