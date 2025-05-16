package com.haiphamcoder.reporting.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.application.service.FolderService;
import com.haiphamcoder.reporting.domain.entity.Folder;
import com.haiphamcoder.reporting.domain.repository.FolderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;

    @Override
    public Folder getFolderById(Long id) {
        return folderRepository.getFolderById(id)
                .orElse(null);
    }

    @Override
    public Folder getFolderByIdAndUserId(Long id, Long userId) {
        return folderRepository.getFolderByIdAndUserId(id, userId)
                .orElse(null);
    }

    @Override
    public List<Folder> getFoldersByUserId(Long userId) {
        return folderRepository.getFoldersByUserId(userId);
    }

    @Override
    public List<Folder> getSubFoldersByParentId(Long parentId) {
        return folderRepository.getFoldersByParentFolderId(parentId);
    }

    @Override
    public Folder saveFolder(Folder folder) {
        return folderRepository.saveFolder(folder);
    }
}
