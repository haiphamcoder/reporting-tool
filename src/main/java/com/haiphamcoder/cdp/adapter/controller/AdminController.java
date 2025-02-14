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

@RestController
@RequestMapping("/admin")
@Tag(name = "admin", description = "Admin controller")
public class AdminController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> get() {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("GET::admin controller"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> post() {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("POST::admin controller"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> put() {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("PUT::admin controller"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> delete() {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("DELETE::admin controller"));
    }

}
