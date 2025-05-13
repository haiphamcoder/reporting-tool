package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.threads.ImportDataSourceManager;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/thread/import-data")
@Tag(name = "thread-import-data", description = "Thread import data controller")
@RequiredArgsConstructor
public class ThreadImportDataSourceController {

    private final ImportDataSourceManager importDataSourceManager;

    @PostMapping(path = "/submit/{source-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> submit(@CookieValue(name = "user-id") String userId,
            @PathVariable("source-id") Long sourceId) {
        try {
            importDataSourceManager.submit(sourceId, false);
            return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Submit successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

}
