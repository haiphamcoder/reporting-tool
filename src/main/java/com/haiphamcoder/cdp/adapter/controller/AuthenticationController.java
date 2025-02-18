package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.service.AuthenticationService;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.shared.ApiResponse;
import com.haiphamcoder.cdp.shared.ApiResponseFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Boolean>> register(@RequestBody RegisterRequest request) {
        if (authenticationService.register(request)) {
            return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse(true));
        }
        return ResponseEntity.ok().body(ApiResponseFactory.createErrorResponse("Register failed"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse(response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken,
            @RequestHeader("user-id") String userId) {
        String newAccessToken = authenticationService.refreshToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.badRequest().body(ApiResponseFactory.createErrorResponse("Invalid refresh token"));
        }
        return ResponseEntity.ok().body(ApiResponseFactory.createSuccessResponse(newAccessToken));
    }

}
