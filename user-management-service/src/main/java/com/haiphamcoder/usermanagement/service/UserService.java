package com.haiphamcoder.usermanagement.service;

import java.util.List;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;

public interface UserService {

    public List<UserDto> getAllUsers();

    public List<UserDto> getAllUsersByProvider(String provider);

    public UserDto getUserByUsername(String username);

    public UserDto getUserByEmail(String email);

    public UserDto getUserById(Long id);

    public UserDto saveUser(UserDto user);
}
