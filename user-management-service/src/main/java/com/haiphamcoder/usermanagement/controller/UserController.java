package com.haiphamcoder.usermanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.model.GetAllUserResponse;
import com.haiphamcoder.usermanagement.domain.model.Metadata;
import com.haiphamcoder.usermanagement.service.UserService;
import com.haiphamcoder.usermanagement.shared.http.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> getAll(@CookieValue(name = "user-id", required = true) Long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        Page<UserDto> users = userService.getAllUsers(userId, page, limit);
        GetAllUserResponse response = GetAllUserResponse.builder()
                .data(users.getContent().stream().map(user -> GetAllUserResponse.Record.builder()
                        .id(user.getId().toString())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .emailVerified(user.isEmailVerified())
                        .role(user.getRole())
                        .avatarUrl(user.getAvatarUrl())
                        .provider(user.getProvider())
                        .build()).toList())
                .metadata(Metadata.builder()
                        .numberOfElements(users.getNumberOfElements())
                        .totalElements(users.getTotalElements())
                        .totalPages(users.getTotalPages())
                        .currentPage(users.getNumber())
                        .pageSize(users.getSize())
                        .build())
                .build();
        return ResponseEntity.ok().body(ApiResponse.success(response, "Get all users successfully"));
    }

    @GetMapping(path = "/provider/{provider}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> getAllByProvider(@PathVariable String provider) {
        List<UserDto> users = userService.getAllUsersByProvider(provider);
        return ResponseEntity.ok().body(ApiResponse.success(users, "Get all users by provider successfully"));
    }

    @GetMapping(path = "/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> getById(@PathVariable(name = "user-id", required = true) Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok().body(ApiResponse.success(user, "Get user by id successfully"));
    }

}
