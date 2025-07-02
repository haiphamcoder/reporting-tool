package com.haiphamcoder.reporting.repository.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    Optional<Source> findByIdAndUserId(Long id, Long userId);

    Long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    @Query("SELECT COUNT(s) FROM Source s WHERE s.userId = :userId AND DATE(s.createdAt) = DATE(:date)")
    Long countByUserIdAndCreatedDate(@Param("userId") Long userId, @Param("date") LocalDate date);

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
        String normalizedSearch = (search != null && search.trim().isEmpty()) ? null : search;
        
        if (normalizedSearch == null) {
            return sourceJpaRepository.findAllByUserIdAndIsDeleted(userId, isDeleted, PageRequest.of(page, limit));
        } else {
            return sourceJpaRepository.findAllByUserIdAndIsDeletedAndNameContainsOrDescriptionContains(userId, isDeleted,
                    normalizedSearch, PageRequest.of(page, limit));
        }
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

        return result;
    }

    @Override
    public Optional<Source> updateSource(Source source) {
        Source savedSource = sourceJpaRepository.save(source);
        return Optional.of(savedSource);
    }

}
