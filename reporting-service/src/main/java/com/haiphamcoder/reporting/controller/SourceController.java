package com.haiphamcoder.reporting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.service.SourceService;
import com.haiphamcoder.reporting.shared.exception.BaseException;
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
    public ResponseEntity<Object> initSource(@CookieValue(name = "user-id") Long userId,
            @RequestBody SourceDto sourceDto) {
        try {
            SourceDto source = sourceService.initSource(userId, sourceDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(source));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @GetMapping()
    public ResponseEntity<Object> getSources(@CookieValue(name = "user-id") Long userId) {
        try {
            List<SourceDto> sources = sourceService.getAllSourcesByUserId(userId);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(sources));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createSource(@CookieValue(name = "user-id") Long userId,
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
    public ResponseEntity<Object> uploadFile(@CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "source-id", required = true) Long sourceId,
            @RequestBody MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(RestAPIResponse.ResponseFactory.createResponse("File is empty"));
            }

            String filePath = sourceService.uploadFile(userId, sourceId, file);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(filePath));
        } catch (BaseException e) {
            RestAPIResponse<Object> apiResponse = RestAPIResponse.ResponseFactory.createResponse(e);
            return ResponseEntity.status(e.getHttpStatus()).body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @GetMapping("/history-upload-file")
    public ResponseEntity<RestAPIResponse<Map<String, String>>> getHistoryUploadFil(
            @CookieValue(name = "user-id") Long userId,
            @RequestParam(name = "connector-type", required = true) Integer connectorType) {
        Map<String, String> historyUploadFile = sourceService.getHistoryUploadFile(userId, connectorType);
        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(historyUploadFile));
    }

    @GetMapping("/confirm-schema")
    public ResponseEntity<Object> confirmSchema(@CookieValue(name = "user-id") Long userId,
            @RequestBody SourceDto sourceDto) {
        try {
            SourceDto updatedSource = sourceService.confirmSchema(userId, sourceDto);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse(updatedSource));
        } catch (BaseException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(RestAPIResponse.ResponseFactory.createResponse(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse<String>> deleteSource(@CookieValue(name = "user-id") Long userId,
            @PathVariable("source-id") Long sourceId) {

        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Source deleted successfully"));
    }

}
