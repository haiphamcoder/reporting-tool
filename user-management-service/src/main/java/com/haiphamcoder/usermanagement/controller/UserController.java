package com.haiphamcoder.usermanagement.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordRequest;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordResponse;
import com.haiphamcoder.usermanagement.domain.model.ChangeRoleRequest;
import com.haiphamcoder.usermanagement.domain.model.ChangeRoleResponse;
import com.haiphamcoder.usermanagement.domain.model.GetAllUserResponse;
import com.haiphamcoder.usermanagement.domain.model.GetUserDetailsResponse;
import com.haiphamcoder.usermanagement.domain.model.Metadata;
import com.haiphamcoder.usermanagement.service.UserService;
import com.haiphamcoder.usermanagement.shared.Pair;
import com.haiphamcoder.usermanagement.shared.http.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class UserController {
        private final UserService userService;

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> getAll(@CookieValue(name = "user-id", required = true) Long userId,
                        @RequestParam(name = "search", required = false, defaultValue = "") String search,
                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                        @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
                Pair<List<UserDto>, Metadata> users = userService.getAllUsers(userId, search, page, limit);
                GetAllUserResponse response = GetAllUserResponse.builder()
                                .data(users.getFirst().stream().map(user -> GetAllUserResponse.Record.builder()
                                                .id(user.getId().toString())
                                                .firstName(user.getFirstName())
                                                .lastName(user.getLastName())
                                                .email(user.getEmail())
                                                .emailVerified(user.isEmailVerified())
                                                .role(user.getRole())
                                                .avatarUrl(user.getAvatarUrl())
                                                .provider(user.getProvider())
                                                .build()).toList())
                                .metadata(users.getSecond())
                                .build();
                return ResponseEntity.ok().body(ApiResponse.success(response, "Get all users successfully"));
        }

        @GetMapping(path = "/provider/{provider}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> getAllByProvider(@PathVariable String provider) {
                List<UserDto> users = userService.getAllUsersByProvider(provider);
                return ResponseEntity.ok().body(ApiResponse.success(users, "Get all users by provider successfully"));
        }

        @GetMapping(path = "/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> getById(
                        @PathVariable(name = "user-id", required = true) Long userId) {
                UserDto user = userService.getUserById(userId);
                GetUserDetailsResponse response = GetUserDetailsResponse.builder()
                                .id(user.getId().toString())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .emailVerified(user.isEmailVerified())
                                .role(user.getRole())
                                .avatarUrl(user.getAvatarUrl())
                                .provider(user.getProvider())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getModifiedAt())
                                .build();
                return ResponseEntity.ok().body(ApiResponse.success(response, "Get user by id successfully"));
        }

        @PutMapping(path = "/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> changePassword(
                        @CookieValue(name = "user-id", required = true) Long userId,
                        @PathVariable(name = "user-id", required = true) Long targetUserId,
                        @RequestBody ChangePasswordRequest request) {
                UserDto user = userService.changePassword(userId, targetUserId, request);
                ChangePasswordResponse response = ChangePasswordResponse.builder()
                                .userId(user.getId().toString())
                                .userName(user.getUsername())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .emailVerified(user.isEmailVerified())
                                .role(user.getRole())
                                .provider(user.getProvider())
                                .providerId(user.getProviderId())
                                .avatarUrl(user.getAvatarUrl())
                                .firstLogin(user.isFirstLogin())
                                .build();
                return ResponseEntity.ok().body(ApiResponse.success(response, "Change password successfully"));
        }

        @PutMapping(path = "/{user-id}/role", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> changeRole(
                        @CookieValue(name = "user-id", required = true) Long userId,
                        @PathVariable(name = "user-id", required = true) Long targetUserId,
                        @RequestBody ChangeRoleRequest request) {
                UserDto user = userService.changeRole(userId, targetUserId, request);
                ChangeRoleResponse response = ChangeRoleResponse.builder()
                                .userId(user.getId().toString())
                                .userName(user.getUsername())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .emailVerified(user.isEmailVerified())
                                .role(user.getRole())
                                .provider(user.getProvider())
                                .providerId(user.getProviderId())
                                .avatarUrl(user.getAvatarUrl())
                                .firstLogin(user.isFirstLogin())
                                .build();
                return ResponseEntity.ok().body(ApiResponse.success(response, "Change role successfully"));
        }

        @DeleteMapping(path = "/{user-id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ApiResponse<Object>> deleteUser(
                        @CookieValue(name = "user-id", required = true) Long userId,
                        @PathVariable(name = "user-id", required = true) Long targetUserId) {
                userService.deleteUser(userId, targetUserId);
                return ResponseEntity.ok().body(ApiResponse.success(null, "Delete user successfully"));
        }

}
