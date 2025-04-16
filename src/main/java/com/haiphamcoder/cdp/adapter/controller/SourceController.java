package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.shared.exception.BaseException;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @PostMapping("/upload-file")
    public ResponseEntity<Object> uploadFile(@CookieValue(name = "user-id") String userId,
            @RequestBody MultipartFile file) {
        try {
            String filePath = sourceService.uploadFile(userId, file);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(filePath));
        } catch (BaseException e) {
            RestAPIResponse<Object> apiResponse = RestAPIResponse.ResponseFactory.createResponse(e);
            return ResponseEntity.status(e.getHttpStatus()).body(apiResponse);
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
