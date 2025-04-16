package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse<String>> deleteSource(@CookieValue(name = "user-id") String userId,
            @PathVariable("source-id") Long sourceId) {

        return ResponseEntity.ok(RestAPIResponse.ResponseFactory.createResponse("Source deleted successfully"));
    }

}
