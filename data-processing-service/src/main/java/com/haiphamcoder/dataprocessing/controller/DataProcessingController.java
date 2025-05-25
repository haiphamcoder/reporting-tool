package com.haiphamcoder.dataprocessing.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto.Mapping;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.service.RawDataService;
import com.haiphamcoder.dataprocessing.service.SchemaSourceService;
import com.haiphamcoder.dataprocessing.shared.http.RestAPIResponse;
import com.haiphamcoder.dataprocessing.threads.ImportDataSourceManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DataProcessingController {

    private final SchemaSourceService schemaSourceService;
    private final ImportDataSourceManager importDataSourceManager;
    private final RawDataService rawDataService;

    @GetMapping("/sources/schema/{id}")
    public ResponseEntity<RestAPIResponse<Object>> getSourceSchema(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId) {
        try {
            List<Mapping> schema = schemaSourceService.getSchema(sourceId);
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(schema));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestAPIResponse.ResponseFactory.createResponse(e.getMessage()));
        }
    }

    @PostMapping("/sources/import/{id}")
    public ResponseEntity<RestAPIResponse<Object>> importSource(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId) {
        try {
            boolean isSuccess = importDataSourceManager.submit(sourceId, true);
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(isSuccess));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestAPIResponse.ResponseFactory.createResponse(e.getMessage()));
        }
    }

    @GetMapping("/sources/{id}/preview")
    public ResponseEntity<RestAPIResponse<Object>> previewSource(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId) {
        try {
            PreviewData previewData = rawDataService.previewSource(sourceId);
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(previewData));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestAPIResponse.ResponseFactory.createResponse(e.getMessage()));
        }
    }

}
