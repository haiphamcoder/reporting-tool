package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;
import com.haiphamcoder.cdp.shared.exception.BaseException;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/source")
@Tag(name = "source", description = "Source controller")
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;

    @GetMapping
    public ResponseEntity<RestAPIResponse<List<Source>>> getSources(@CookieValue(name = "user-id") String userId) {
        List<Source> sources = sourceService.getAllSourcesByUserId(Long.parseLong(userId));
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(sources));
    }

    @PostMapping()
    public ResponseEntity<Object> createSource(@CookieValue(name = "user-id") String userId,
            @RequestBody SourceDto sourceDto) {
        try {
            sourceService.createSource(sourceDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Source created successfully"));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @PostMapping("/upload-file")
    public ResponseEntity<Object> uploadFile(@CookieValue(name = "user-id") String userId,
            @RequestParam(name = "connector-type", required = true) Integer connectorType,
            @RequestBody MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(RestAPIResponse.ResponseFactory.createResponse("File is empty"));
            }

            String filePath = sourceService.uploadFile(userId, connectorType, file);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(filePath));
        } catch (BaseException e) {
            RestAPIResponse<Object> apiResponse = RestAPIResponse.ResponseFactory.createResponse(e);
            return ResponseEntity.status(e.getHttpStatus()).body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @GetMapping("/history-upload-file")
    public ResponseEntity<RestAPIResponse<Map<String, String>>> getHistoryUploadFil(
            @CookieValue(name = "user-id") String userId,
            @RequestParam(name = "connector-type", required = true) Integer connectorType) {
        Map<String, String> historyUploadFile = sourceService.getHistoryUploadFile(userId, connectorType);
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(historyUploadFile));
    }

    @GetMapping("/preview-data")
    public ResponseEntity<Object> getPreviewData(
            @CookieValue(name = "user-id") String userId,
            @RequestBody PreviewDataRequest previewDataRequest) {
        try {
            PreviewData previewData = sourceService.getPreviewData(userId, previewDataRequest);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(previewData));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @GetMapping("/confirm-schema")
    public ResponseEntity<Object> confirmSchema(@CookieValue(name = "user-id") String userId,
            @RequestParam(name = "source-id", required = true) Long sourceId,
            @RequestBody Map<String, String> mapping) {
        try {
            sourceService.confirmSchema(userId, sourceId, mapping);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Schema confirmed successfully"));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse<String>> deleteSource(@CookieValue(name = "user-id") String userId,
            @PathVariable("source-id") Long sourceId) {

        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Source deleted successfully"));
    }

}
