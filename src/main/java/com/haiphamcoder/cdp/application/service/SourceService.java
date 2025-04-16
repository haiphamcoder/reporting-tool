package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    public List<Source> getAllSourcesByUserId(Long userId) {
        return sourceRepository.getAllSourcesByUserId(userId);
    }

    public Source deleteSourceById(Long id) {
        Optional<Source> deletedSource = sourceRepository.deleteSourceById(id);
        if (deletedSource.isPresent()) {
            return deletedSource.get();
        }
        throw new RuntimeException("Source not found");
    }

    public Source createSource(Source source) {
        Optional<Source> createdSource = sourceRepository.createSource(source);
        if (createdSource.isPresent()) {
            return createdSource.get();
        }
        throw new RuntimeException("Create source failed");
    }

}