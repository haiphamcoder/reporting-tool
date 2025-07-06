package com.haiphamcoder.dataprocessing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;
import com.haiphamcoder.dataprocessing.domain.model.GetChartPreviewDataRequest;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.domain.model.request.UpdateSourceDataRequest;
import com.haiphamcoder.dataprocessing.service.RawDataService;
import com.haiphamcoder.dataprocessing.service.SchemaSourceService;
import com.haiphamcoder.dataprocessing.shared.http.ApiResponse;
import com.haiphamcoder.dataprocessing.threads.ImportDataSourceManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class DataProcessingController {

    private final SchemaSourceService schemaSourceService;
    private final ImportDataSourceManager importDataSourceManager;
    private final RawDataService rawDataService;

    @GetMapping("/sources/schema/{id}")
    public ResponseEntity<ApiResponse<Object>> getSourceSchema(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId) {
        List<Mapping> schema = schemaSourceService.getSchema(sourceId);
        return ResponseEntity.ok().body(ApiResponse.success(schema, "Source schema fetched successfully"));
    }

    @PostMapping("/sources/import/{id}")
    public ResponseEntity<ApiResponse<Object>> importSource(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId) {
        boolean isSuccess = importDataSourceManager.submit(sourceId, true);
        return ResponseEntity.ok().body(ApiResponse.success(isSuccess, "Source imported successfully"));
    }

    @PutMapping("/sources/{id}/data")
    public ResponseEntity<ApiResponse<Object>> updateSourceData(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId,
            @RequestBody(required = true) UpdateSourceDataRequest request) {
        log.info("request: {}", request.toString());
        rawDataService.updateSourceData(sourceId, request);
        return ResponseEntity.ok().body(ApiResponse.success(null, "Source data updated successfully"));
    }

    @GetMapping("/sources/{id}/preview")
    public ResponseEntity<ApiResponse<Object>> previewSource(
            @CookieValue(value = "user-id", required = true) Long userId,
            @PathVariable("id") Long sourceId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        PreviewData previewData = rawDataService.previewSource(sourceId, page, limit);
        return ResponseEntity.ok().body(ApiResponse.success(previewData, "Source previewed successfully"));
    }

    @PostMapping("/charts/preview-data")
    public ResponseEntity<ApiResponse<Object>> getPreviewData(
            @CookieValue(value = "user-id", required = true) Long userId,
            @RequestBody(required = true) GetChartPreviewDataRequest request,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        PreviewData previewData = rawDataService.getChartPreviewData(request, page, limit);
        return ResponseEntity.ok().body(ApiResponse.success(previewData, "Preview data fetched successfully"));
    }

}
