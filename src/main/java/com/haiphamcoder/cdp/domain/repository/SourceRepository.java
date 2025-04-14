package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Source;

public interface SourceRepository {
    Optional<Source> getSourceById(Long id);

    List<Source> getAllSourcesByUserIdAndFolderId(Long userId, Long folderId);

    List<Source> getAllSourcesByUserIdAndFolderIdAndKeyword(Long userId, Long folderId, String keyword);

    List<Source> getAllSourcesByUserIdAndFolderIdAndConnectorType(Long userId, Long folderId, Integer connectorType);

    List<Source> getAllSourcesByUserIdAndFolderIdAndStatus(Long userId, Long folderId, Integer status);

    List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimit(Long userId, Long folderId, Integer page, Integer limit);

    List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndKeyword(Long userId, Long folderId, Integer page, Integer limit, String keyword);

    List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndConnectorType(Long userId, Long folderId, Integer page, Integer limit, Integer connectorType);
     
}