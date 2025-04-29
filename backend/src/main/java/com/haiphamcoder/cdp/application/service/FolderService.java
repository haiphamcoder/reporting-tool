package com.haiphamcoder.cdp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.entity.Folder;
import com.haiphamcoder.cdp.domain.repository.FolderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;

    public Folder getFolderById(Long id) {
        return folderRepository.getFolderById(id)
                .orElse(null);
    }

    public Folder getFolderByIdAndUserId(Long id, Long userId) {
        return folderRepository.getFolderByIdAndUserId(id, userId)
                .orElse(null);
    }

    public List<Folder> getFoldersByUserId(Long userId) {
        return folderRepository.getFoldersByUserId(userId);
    }

    public List<Folder> getSubFoldersByParentId(Long parentId) {
        return folderRepository.getFoldersByParentFolderId(parentId);
    }

    public Folder saveFolder(Folder folder) {
        return folderRepository.saveFolder(folder);
    }
}
