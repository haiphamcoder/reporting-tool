package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.application.service.CSVProcessingService;
import com.haiphamcoder.cdp.application.service.ExcelProcessingService;
import com.haiphamcoder.cdp.application.service.HdfsFileService;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "file", description = "File controller")
@RequiredArgsConstructor
public class HdfsFileController {
    private final HdfsFileService hdfsFileService;
    private final CSVProcessingService csvProcessingService;
    private final ExcelProcessingService excelProcessingService;
    
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestAPIResponse<String>> uploadFile(
            @CookieValue(name = "user-id", required = true) String userId,
            @RequestBody MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createErrorResponse("File name is required"));
        }
        String filePath;
        try {
            filePath = hdfsFileService.uploadFile(userId, file.getInputStream(), fileName.trim().replaceAll("\\s+", "_"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createErrorResponse("Upload file failed"));
        }
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(filePath));
    }

    @GetMapping(path = "/schema")
    public ResponseEntity<RestAPIResponse<List<String>>> getSchema(
        @CookieValue(name = "user-id", required = true) String userId,
        @RequestParam(name = "file-name", required = true) String fileName
    ) {
        List<String> schema = csvProcessingService.getSchema(userId, fileName);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(schema));
    }

}
