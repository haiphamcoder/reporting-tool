package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.domain.repository.HdfsRepository;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "file", description = "File controller")
@RequiredArgsConstructor
public class FileController {
    private final HdfsRepository hdfsRepository;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestAPIResponse<String>> uploadFile(
            @RequestHeader(name = "user-id", required = true) String userId, @RequestBody MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createErrorResponse("File name is required"));
        }
        String filePath;
        try {
            filePath = hdfsRepository.uploadFile(file.getInputStream(), fileName.trim().replaceAll("\\s+", "_"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createErrorResponse("Upload file failed"));
        }
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(filePath));
    }

}
