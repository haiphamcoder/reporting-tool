package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "admin", description = "Admin controller")
public class AdminController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> get(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse("GET::admin controller"));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> post(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createResponse("POST::admin controller"));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> put(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse("PUT::admin controller"));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> delete(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createResponse("DELETE::admin controller"));
    }

}
