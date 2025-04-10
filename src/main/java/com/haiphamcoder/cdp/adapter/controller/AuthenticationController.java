package com.haiphamcoder.cdp.adapter.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.application.service.AuthenticationService;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.shared.exception.BaseException;
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
            return ResponseEntity.badRequest()
                    .body(RestAPIResponse.ResponseFactory.createResponse("Register failed"));
        }
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createResponse("Register successfully"));
    }

    @PostMapping(path = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> authenticate(HttpServletResponse response,
            @RequestBody AuthenticationRequest request) {
        try {
            RestAPIResponse<String> authenResponse = authenticationService.authenticate(request, response);
            return ResponseEntity.ok()
                    .body(authenResponse);
        } catch (BaseException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            RestAPIResponse<Object> apiResponse = RestAPIResponse.ResponseFactory.createResponse(e);
            return ResponseEntity.status(e.getHttpStatus()).body(apiResponse);
        } catch (Exception e) {
            log.error("Error during authentication", e);
            return ResponseEntity.internalServerError()
                    .body(RestAPIResponse.ResponseFactory.internalServerErrorResponse());
        }
    }

    @GetMapping(path = "/me")
    public ResponseEntity<Object> getUserInfo(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok(Map.of("user_id", userId));
    }

}
