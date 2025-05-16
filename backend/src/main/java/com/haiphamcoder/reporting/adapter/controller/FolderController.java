package com.haiphamcoder.reporting.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.reporting.adapter.dto.FolderDto;
import com.haiphamcoder.reporting.adapter.dto.mapper.FolderMapper;
import com.haiphamcoder.reporting.application.service.FolderService;
import com.haiphamcoder.reporting.domain.entity.Folder;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/folder")
@Tag(name = "folder", description = "Folder controller")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @GetMapping(name = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<RestAPIResponse<FolderDto>> getFolderById(@CookieValue(name = "user-id") String userId, @PathVariable("id") String folderId) {
        Folder folder = folderService.getFolderByIdAndUserId(Long.parseLong(folderId), Long.parseLong(userId));
        if (folder == null) {
            return ResponseEntity.badRequest().body(RestAPIResponse.ResponseFactory.createResponse("Folder not found"));
        }
        FolderDto folderDto = FolderMapper.getInstance().toDto(folder);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(folderDto));
    }
}
