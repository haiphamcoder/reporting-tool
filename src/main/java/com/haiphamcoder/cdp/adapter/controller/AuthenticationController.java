package com.haiphamcoder.cdp.adapter.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.service.AuthenticationService;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<Boolean>> register(@RequestBody RegisterRequest request) {
        if (authenticationService.register(request)) {
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(true));
        }
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createErrorResponse("Register failed"));
    }

    @PostMapping(path = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(response));
    }

    @PostMapping(path = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<Object>> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken,
            @RequestHeader("user-id") String userId) {
        String newAccessToken = authenticationService.refreshToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createUnauthorizedResponse("Invalid refresh token"));
        }
        Map<String, String> body = Map.of("access_token", newAccessToken);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(body));
    }

}
