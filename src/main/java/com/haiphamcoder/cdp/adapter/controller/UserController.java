package com.haiphamcoder.cdp.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.cdp.adapter.dto.UserDto;
import com.haiphamcoder.cdp.adapter.dto.mapper.UserMapper;
import com.haiphamcoder.cdp.application.service.UserService;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "user", description = "User controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<UserDto>> get(@CookieValue(name = "user-id", required = true) String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        if (user == null) {
            return ResponseEntity.badRequest().body(RestAPIResponse.ResponseFactory.createErrorResponse("User not found"));
        }
        UserDto userDto = UserMapper.toDto(user);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse(userDto));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> post(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createSuccessResponse("POST::user controller"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> put(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createSuccessResponse("PUT::user controller"));
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> delete(@RequestHeader("user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createSuccessResponse("DELETE::user controller"));
    }
}
