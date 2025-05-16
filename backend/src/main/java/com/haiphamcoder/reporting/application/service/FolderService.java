package com.haiphamcoder.reporting.application.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.entity.Folder;

public interface FolderService {
    public Folder getFolderById(Long id);

    public Folder getFolderByIdAndUserId(Long id, Long userId);

    public List<Folder> getFoldersByUserId(Long userId);

    public List<Folder> getSubFoldersByParentId(Long parentId);

    public Folder saveFolder(Folder folder);
}
