package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

@RestController
public class IndexController{
    @GetMapping("/")
    public ResponseEntity<RestAPIResponse<String>> greeting() {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse("Hello, world!"));
    }
}
