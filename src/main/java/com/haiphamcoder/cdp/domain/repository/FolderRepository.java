package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.Folder;

public interface FolderRepository {
    Optional<Folder> getFolderById(Long id);

    Optional<Folder> getFolderByIdAndUserId(Long id, Long userId);

    List<Folder> getFoldersByUserId(Long userId);

    List<Folder> getFoldersByParentFolderId(Long parentFolderId);

    Folder saveFolder(Folder folder);
    
}