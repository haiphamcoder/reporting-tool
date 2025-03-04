package com.haiphamcoder.cdp.adapter.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.service.AuthenticationService;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.AuthenticationResponse;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.CookieUtils;
import com.haiphamcoder.cdp.shared.StringUtils;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<Object>> register(@RequestBody RegisterRequest request) {
        User registeredUser = authenticationService.register(request);
        if (registeredUser == null) {
            return ResponseEntity.badRequest().body(RestAPIResponse.ResponseFactory.createErrorResponse("Register failed"));
        }
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(Map.of("user_id", registeredUser.getId())));
    }

    @PostMapping(path = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<AuthenticationResponse>> authenticate(HttpServletResponse response,
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse authenResponse = authenticationService.authenticate(request);
        if (authenResponse.getStatus() == CommonConstants.AUTHEN_SUCCESS) {
            CookieUtils.addCookie(response, "user-id", String.valueOf(authenResponse.getUserId()));
            CookieUtils.addCookie(response, "access-token", authenResponse.getAccessToken());
            CookieUtils.addCookie(response, "refresh-token", authenResponse.getRefreshToken());
            return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(authenResponse));
        } else {
            CookieUtils.deleteCookie(response, "user-id");
            CookieUtils.deleteCookie(response, "access-token");
            CookieUtils.deleteCookie(response, "refresh-token");
        }
        return ResponseEntity.badRequest().body(RestAPIResponse.ResponseFactory.createErrorResponse("Authentication failed"));
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

    @GetMapping(path = "/me")
    public ResponseEntity<Object> getUserInfo(@CookieValue(name = "user-id", required = false) String userId) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not authenticated. Please login to get user info");
        }
        return ResponseEntity.ok(Map.of("user_id", userId));
    }

}
