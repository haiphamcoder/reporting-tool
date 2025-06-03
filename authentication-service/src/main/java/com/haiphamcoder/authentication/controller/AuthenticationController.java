package com.haiphamcoder.authentication.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.authentication.service.AuthenticationService;
import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.domain.model.AuthenticationRequest;
import com.haiphamcoder.authentication.domain.model.RegisterRequest;
import com.haiphamcoder.authentication.shared.http.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/")
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request) {
        UserDto registeredUser = authenticationService.register(request);
        return ResponseEntity.ok().body(ApiResponse.success(registeredUser, "Register successfully"));
    }

    @PostMapping(path = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> authenticate(HttpServletResponse response,
            @RequestBody AuthenticationRequest request) {
        boolean authenResponse = authenticationService.authenticate(request, response);
        return ResponseEntity.ok().body(ApiResponse.success(authenResponse, "Authenticate successfully"));
    }

    @GetMapping(path = "/me")
    public ResponseEntity<Object> getUserInfo(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok(Map.of("user_id", userId));
    }

}
