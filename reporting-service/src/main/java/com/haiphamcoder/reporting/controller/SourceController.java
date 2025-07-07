package com.haiphamcoder.reporting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.reporting.config.CommonConstants;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.dto.SourceDto.UserSourcePermission;
import com.haiphamcoder.reporting.domain.model.request.ConfirmSheetRequest;
import com.haiphamcoder.reporting.domain.model.request.InitSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.UpdateSourceRequest;
import com.haiphamcoder.reporting.domain.model.response.GetAllSourcesResponse;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.service.SourceService;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.http.ApiResponse;
import com.haiphamcoder.reporting.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/sources")
@RequiredArgsConstructor
public class SourceController {
    
    private final SourceService sourceService;

    @PostMapping(path = "/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> initSource(@CookieValue(name = "user-id") Long userId,
            @RequestBody InitSourceRequest sourceDto) {
        SourceDto source = sourceService.initSource(userId, sourceDto);
        return ResponseEntity.ok(ApiResponse.success(source, "Source initialized successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Object>> getSources(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
        
        Pair<List<SourceDto>, Metadata> sources = sourceService.getAllSourcesByUserId(userId, search, page, limit);
                
        GetAllSourcesResponse response = GetAllSourcesResponse.builder()
                .data(sources.getFirst().stream().map(source -> GetAllSourcesResponse.Record.builder()
                        .id(source.getId())
                        .name(source.getName())
                        .description(source.getDescription())
                        .type(source.getConnectorType())
                        .owner(source.getOwner())
                        .canEdit(source.getCanEdit())
                        .canShare(source.getCanShare())
                        .status(CommonConstants.SOURCE_STATUS_MAP.get(source.getStatus()))
                        .createdAt(source.getCreatedAt())
                        .updatedAt(source.getModifiedAt())
                        .build())
                        .toList())
                .metadata(sources.getSecond())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Sources fetched successfully"));
    }

    @GetMapping("/{source-id}/share")
    public ResponseEntity<ApiResponse<Object>> getShare(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId) {
        List<UserSourcePermission> shareSource = sourceService.getShareSource(userId, sourceId);
        return ResponseEntity.ok(ApiResponse.success(shareSource, "Share source fetched successfully"));
    }

    @PostMapping("/{source-id}/share")
    public ResponseEntity<ApiResponse<Object>> shareSource(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId,
            @RequestBody ShareSourceRequest shareSourceRequest) {
        sourceService.shareSource(userId, sourceId, shareSourceRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Source shared successfully"));
    }

    @GetMapping("/{source-id}")
    public ResponseEntity<ApiResponse<Object>> getSourceById(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId) {
        SourceDto source = sourceService.getSourceById(userId, sourceId);
        return ResponseEntity.ok(ApiResponse.success(source, "Source fetched successfully"));
    }

    @PutMapping("/{source-id}")
    public ResponseEntity<ApiResponse<Object>> updateSource(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId,
            @RequestBody UpdateSourceRequest updateSourceRequest) {
        SourceDto updatedSource = sourceService.updateSource(userId, sourceId, updateSourceRequest);
        return ResponseEntity.ok(ApiResponse.success(updatedSource, "Source updated successfully"));
    }

    @PostMapping("/upload-file")
    public ResponseEntity<ApiResponse<Object>> uploadFile(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "source-id", required = true) Long sourceId,
            @RequestBody MultipartFile file) {
        String filePath = sourceService.uploadFile(userId, sourceId, file);
        return ResponseEntity.ok(ApiResponse.success(filePath, "File uploaded successfully"));
    }

    @PostMapping("/{source-id}/confirm-sheet")
    public ResponseEntity<ApiResponse<Object>> confirmSheet(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId,
            @RequestBody ConfirmSheetRequest confirmSheetRequest) {
        sourceService.confirmSheet(userId, sourceId, confirmSheetRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Sheet confirmed successfully"));
    }

    @GetMapping("/history-upload-file")
    public ResponseEntity<RestAPIResponse<Object>> getHistoryUploadFile(
            @CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "connector-type", required = true) Integer connectorType) {
        Map<String, String> historyUploadFile = sourceService.getHistoryUploadFile(userId, connectorType);
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(historyUploadFile));
    }

    @PostMapping("/confirm-schema")
    public ResponseEntity<ApiResponse<Object>> confirmSchema(@CookieValue(name = "user-id") Long userId,
            @RequestBody SourceDto sourceDto) {
        SourceDto updatedSource = sourceService.confirmSchema(userId, sourceDto);
        return ResponseEntity.ok(ApiResponse.success(updatedSource, "Schema confirmed successfully"));
    }

    @DeleteMapping("/{source-id}")
    public ResponseEntity<ApiResponse<Object>> deleteSource(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId) {
        sourceService.deleteSource(userId, sourceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Source deleted successfully"));
    }

    @GetMapping("/{source-id}/clone")
    public ResponseEntity<ApiResponse<Object>> cloneSource(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId) {
        sourceService.cloneSource(userId, sourceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Source cloned successfully"));
    }

}
