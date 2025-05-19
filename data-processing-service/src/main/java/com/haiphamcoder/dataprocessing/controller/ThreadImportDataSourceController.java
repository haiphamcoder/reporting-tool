package com.haiphamcoder.dataprocessing.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.dataprocessing.shared.http.RestAPIResponse;
import com.haiphamcoder.dataprocessing.threads.ImportDataSourceManager;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/thread/import-data")
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
