package com.haiphamcoder.reporting.adapter.persistence;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface SourceJpaRepository extends JpaRepository<Source, Long> {
    List<Source> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    Optional<Source> findByIdAndUserIdAndFolderId(Long id, Long userId, Long folderId);

    List<Source> findAllByUserIdAndFolderId(Long userId, Long folderId);

    List<Source> findAllByUserIdAndFolderIdAndConnectorType(Long userId, Long folderId, Integer connectorType);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(s) FROM Source s WHERE s.user.id = :userId AND DATE(s.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM Source s WHERE s.user.id = :userId AND s.name = :sourceName")
    Long countByUserIdAndName(@Param("userId") Long userId, @Param("sourceName") String sourceName);
}

@Component
@RequiredArgsConstructor
@Slf4j
public class SourceRepositoryImpl implements SourceRepository {

    private final SourceJpaRepository sourceJpaRepository;

    @Override
    public Boolean checkSourceName(String userId, String sourceName) {
        return sourceJpaRepository.countByUserIdAndName(Long.parseLong(userId), sourceName) > 0;
    }

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

    @Override
    public Long getTotalSourceByUserIdAndIsDeleted(Long userId, Boolean isDeleted) {
        return sourceJpaRepository.countByUserIdAndIsDeleted(userId, isDeleted);
    }

    @Override
    public List<Long> getSourceCountByLast30Days(Long userId) {
        List<Long> result = new LinkedList<>();
        LocalDate today = LocalDate.now();

        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            Long count = sourceJpaRepository.countByUserIdAndCreatedDate(userId, date);
            result.add(count);
        }

        // reverse the list
        Collections.reverse(result);
        return result;
    }

    @Override
    public Optional<Source> updateSource(Source source) {
        Source savedSource = sourceJpaRepository.save(source);
        return Optional.of(savedSource);
    }
    
}
