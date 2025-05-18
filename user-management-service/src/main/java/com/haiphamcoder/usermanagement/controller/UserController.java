package com.haiphamcoder.usermanagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.mapper.UserMapper;
import com.haiphamcoder.usermanagement.service.UserService;
import com.haiphamcoder.usermanagement.shared.http.RestAPIResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<List<UserDto>>> getAll() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream().map(UserMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(userDtos));
    }

    @GetMapping(path = "/get-all/provider/{provider}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<List<UserDto>>> getAllByProvider(@PathVariable String provider) {
        List<User> users = userService.getAllUsersByProvider(provider);
        List<UserDto> userDtos = users.stream().map(UserMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(userDtos));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<UserDto>> get(@CookieValue(name = "user-id") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        if (user == null) {
            return ResponseEntity.badRequest().body(RestAPIResponse.ResponseFactory.createResponse("User not found"));
        }
        UserDto userDto = UserMapper.toDto(user);
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse(userDto));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> post(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createResponse("POST::user controller"));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> put(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok().body(RestAPIResponse.ResponseFactory.createResponse("PUT::user controller"));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestAPIResponse<String>> delete(@CookieValue(name = "user-id") String userId) {
        return ResponseEntity.ok()
                .body(RestAPIResponse.ResponseFactory.createResponse("DELETE::user controller"));
    }
}
