package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

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

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> get(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse("GET::admin controller"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> post(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createSuccessResponse("POST::admin controller"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> put(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse("PUT::admin controller"));
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> delete(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createSuccessResponse("DELETE::admin controller"));
    }

}
