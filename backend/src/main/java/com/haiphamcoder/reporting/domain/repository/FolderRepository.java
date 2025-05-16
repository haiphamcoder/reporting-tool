package com.haiphamcoder.reporting.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.reporting.domain.entity.Folder;

public interface FolderRepository {
    Optional<Folder> getFolderById(Long id);

    Optional<Folder> getFolderByIdAndUserId(Long id, Long userId);

    List<Folder> getFoldersByUserId(Long userId);

    List<Folder> getFoldersByParentFolderId(Long parentFolderId);

    Folder saveFolder(Folder folder);
    
}