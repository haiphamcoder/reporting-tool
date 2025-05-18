package com.haiphamcoder.authentication.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.authentication.service.AuthenticationService;
import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.domain.model.AuthenticationRequest;
import com.haiphamcoder.authentication.domain.model.RegisterRequest;
import com.haiphamcoder.authentication.shared.exception.BaseException;
import com.haiphamcoder.authentication.shared.http.RestAPIResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<Object>> register(@RequestBody RegisterRequest request) {
        UserDto registeredUser = authenticationService.register(request);
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
