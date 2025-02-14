package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.ApiResponse;
import com.haiphamcoder.cdp.shared.ApiResponseFactory;

@RestController
public class IndexController{
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> greeting() {
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse("Hello, world!"));
    }
}
