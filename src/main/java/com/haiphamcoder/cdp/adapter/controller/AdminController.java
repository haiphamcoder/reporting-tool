package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.ApiResponse;
import com.haiphamcoder.cdp.shared.ApiResponseFactory;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "admin", description = "Admin controller")
public class AdminController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> get(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("GET::admin controller"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> post(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("POST::admin controller"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> put(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("PUT::admin controller"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> delete(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("DELETE::admin controller"));
    }

}
