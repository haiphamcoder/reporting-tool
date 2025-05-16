package com.haiphamcoder.reporting.adapter.dto.mapper;

import org.springframework.stereotype.Component;

import com.haiphamcoder.reporting.adapter.dto.FolderDto;
import com.haiphamcoder.reporting.application.service.FolderService;
import com.haiphamcoder.reporting.application.service.UserService;
import com.haiphamcoder.reporting.domain.entity.Folder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FolderMapper {
    private final UserService userService;
    private final FolderService folderService;

    @Getter
    private static FolderMapper instance;

    public Folder toEntity(FolderDto folderDto) {
        return Folder.builder()
                .id(folderDto.getId())
                .name(folderDto.getName())
                .description(folderDto.getDescription())
                .parentFolder(folderService.getFolderById(folderDto.getParentFolderId()))
                .subFolders(folderService.getSubFoldersByParentId(folderDto.getId()))
                .user(userService.getUserById(folderDto.getUserId()))
                .isDeleted(folderDto.getIsDeleted())
                .isStarred(folderDto.getIsStarred())
                .build();
    }

    public FolderDto toDto(Folder folder) {
        return FolderDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .parentFolderId(folder.getParentFolder() == null ? null : folder.getParentFolder().getId())
                .userId(folder.getUser().getId())
                .isDeleted(folder.getIsDeleted())
                .isStarred(folder.getIsStarred())
                .build();
    }

}
