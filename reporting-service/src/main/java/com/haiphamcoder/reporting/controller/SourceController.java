package com.haiphamcoder.reporting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.service.SourceService;
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
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/sources")
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;

    @PostMapping(path = "/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> initSource(@CookieValue(name = "user-id") Long userId,
            @RequestBody SourceDto sourceDto) {
        SourceDto source = sourceService.initSource(userId, sourceDto);
        return ResponseEntity.ok(ApiResponse.success(source, "Source initialized successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<SourceDto>>> getSources(@CookieValue(name = "user-id") Long userId) {
        List<SourceDto> sources = sourceService.getAllSourcesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(sources, "Sources fetched successfully"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> createSource(@CookieValue(name = "user-id") Long userId,
            @RequestBody SourceDto sourceDto) {
        sourceService.createSource(sourceDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Source created successfully"));
    }

    @PostMapping("/upload-file")
    public ResponseEntity<ApiResponse<Object>> uploadFile(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "source-id", required = true) Long sourceId,
            @RequestBody MultipartFile file) {
        String filePath = sourceService.uploadFile(userId, sourceId, file);
        return ResponseEntity.ok(ApiResponse.success(filePath, "File uploaded successfully"));
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
        sourceService.deleteSource(sourceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Source deleted successfully"));
    }

}
