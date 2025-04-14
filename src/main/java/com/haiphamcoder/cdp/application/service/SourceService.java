package com.haiphamcoder.cdp.application.service;

import java.util.List;

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

    public List<Source> getAllSourcesByUserIdAndFolderId(Long userId, Long folderId) {
        return sourceRepository.getAllSourcesByUserIdAndFolderId(userId, folderId);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndKeyword(Long userId, Long folderId, String keyword) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndKeyword(userId, folderId, keyword);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndConnectorType(Long userId, Long folderId,
            Integer connectorType) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndConnectorType(userId, folderId, connectorType);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndStatus(Long userId, Long folderId, Integer status) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndStatus(userId, folderId, status);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimit(Long userId, Long folderId, Integer page,
            Integer limit) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndPageAndLimit(userId, folderId, page, limit);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndKeyword(Long userId, Long folderId,
            Integer page, Integer limit, String keyword) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndKeyword(userId, folderId, page, limit,
                keyword);
    }

    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndConnectorType(Long userId, Long folderId,
            Integer page, Integer limit, Integer connectorType) {
        return sourceRepository.getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndConnectorType(userId, folderId, page,
                limit, connectorType);
    }

}