package com.haiphamcoder.cdp.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface SourceJpaRepository extends JpaRepository<Source, Long> {
    List<Source> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Optional<Source> findByIdAndUserIdAndFolderId(Long id, Long userId, Long folderId);

    List<Source> findAllByUserIdAndFolderId(Long userId, Long folderId);

    List<Source> findAllByUserIdAndFolderIdAndConnectorType(Long userId, Long folderId, Integer connectorType);
}

@Component
@RequiredArgsConstructor
@Slf4j
public class SourceRepositoryImpl implements SourceRepository {

    private final SourceJpaRepository sourceJpaRepository;

    @Override
    public Optional<Source> getSourceById(Long id) {
        return sourceJpaRepository.findById(id);
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndIsDeleted(Long userId, Boolean isDeleted) {
        return sourceJpaRepository.findAllByUserIdAndIsDeleted(userId, isDeleted);
    }

    @Override
    public Optional<Source> deleteSourceById(Long id) {
        Optional<Source> source = sourceJpaRepository.findById(id);
        if (source.isPresent()) {
            source.get().setIsDeleted(true);
            sourceJpaRepository.save(source.get());
        }
        return source;
    }

    @Override
    public Optional<Source> createSource(Source source) {
        sourceJpaRepository.save(source);
        return Optional.of(source);
    }

}
