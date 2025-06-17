package com.haiphamcoder.usermanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;

public interface UserService {

    public Page<UserDto> getAllUsers(Long userId, Integer page, Integer limit);

    public List<UserDto> getAllUsersByProvider(String provider);

    public UserDto getUserByUsername(String username);

    public UserDto getUserByEmail(String email);

    public UserDto getUserById(Long id);

    public UserDto saveUser(UserDto user);
}
