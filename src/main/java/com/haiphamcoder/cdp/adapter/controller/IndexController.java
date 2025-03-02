package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

@RestController
public class IndexController {
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> greeting() {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse("Hello, world!"));
    }
}
