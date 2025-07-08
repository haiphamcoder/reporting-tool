package com.haiphamcoder.reporting.repository.impl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface SourceJpaRepository extends JpaRepository<Source, Long> {
    @Query("SELECT s FROM Source s WHERE s.userId = :userId AND s.isDeleted = :isDeleted AND (s.name LIKE %:search% OR s.description LIKE %:search%)")
    Page<Source> findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, @Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Source s WHERE s.userId = :userId AND s.isDeleted = :isDeleted")
    Page<Source> findAllByUserIdAndIsDeleted(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, Pageable pageable);

    @Query("SELECT s FROM Source s WHERE (s.userId = :userId OR s.id IN :sourceIds) AND s.isDeleted = false AND (s.name LIKE %:search% OR s.description LIKE %:search%)")
    Page<Source> findAllByUserIdOrSourceId(
            @Param("userId") Long userId,
            @Param("sourceIds") Set<Long> sourceIds,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COUNT(s) FROM Source s WHERE (s.userId = :userId OR s.id IN :sourceIds) AND s.isDeleted = :isDeleted")
    Long countByUserIdOrSourceIdAndIsDeleted(
            @Param("userId") Long userId,
            @Param("sourceIds") Set<Long> sourceIds,
            @Param("isDeleted") Boolean isDeleted);

    Optional<Source> findByIdAndUserId(Long id, Long userId);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(s) FROM Source s WHERE s.userId = :userId AND DATE(s.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM Source s WHERE (s.userId = :userId OR s.id IN :sourceIds) AND s.isDeleted = :isDeleted AND DATE(s.createdAt) = DATE(:date)")
    Long countByUserIdOrSourceIdAndIsDeletedAndCreatedDate(@Param("userId") Long userId,
            @Param("sourceIds") Set<Long> sourceIds,
            @Param("isDeleted") Boolean isDeleted,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM Source s WHERE s.userId = :userId AND s.name = :sourceName AND s.isDeleted = false")
    Long countByUserIdAndName(@Param("userId") Long userId, @Param("sourceName") String sourceName);

    @Query("SELECT s FROM Source s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Source> findByIdAndIsDeleted(@Param("id") Long id);
}

@Component
@RequiredArgsConstructor
@Slf4j
public class SourceRepositoryImpl implements SourceRepository {

    private final SourceJpaRepository sourceJpaRepository;

    @Override
    public boolean checkSourceName(Long userId, String sourceName) {
        return sourceJpaRepository.countByUserIdAndName(userId, sourceName) > 0;
    }

    @Override
    public Optional<Source> getSourceById(Long id) {
        return sourceJpaRepository.findByIdAndIsDeleted(id);
    }

    @Override
    public Page<Source> getAllSourcesByUserIdAndIsDeleted(Long userId, Boolean isDeleted, String search, Integer page,
            Integer limit) {
        return sourceJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId,
                isDeleted, search, PageRequest.of(page, limit));
    }

    @Override
    public Page<Source> getAllSourcesByUserIdOrSourceId(Long userId, Set<Long> sourceIds, String search, Integer page,
            Integer limit) {
        return sourceJpaRepository.findAllByUserIdOrSourceId(userId, sourceIds, search, PageRequest.of(page, limit));
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
    public Long getSourceCountByUserIdOrSourceIdAndIsDeletedAndCreatedDate(Long userId, Set<Long> sourceIds,
            Boolean isDeleted, LocalDate date) {
        return sourceJpaRepository.countByUserIdOrSourceIdAndIsDeletedAndCreatedDate(userId, sourceIds, isDeleted, date);
    }

    @Override
    public Optional<Source> updateSource(Source source) {
        Source savedSource = sourceJpaRepository.save(source);
        return Optional.of(savedSource);
    }

    @Override
    public Long getTotalSourceByUserIdOrSourceIdAndIsDeleted(Long userId, Set<Long> sourceIds, Boolean isDeleted) {
        return sourceJpaRepository.countByUserIdOrSourceIdAndIsDeleted(userId, sourceIds, isDeleted);
    }

}
