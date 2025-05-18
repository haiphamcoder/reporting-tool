package com.haiphamcoder.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haiphamcoder.usermanagement.service.UserService;
import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.mapper.UserMapper;
import com.haiphamcoder.usermanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getAllUsersByProvider(String provider) {
        List<User> users = userRepository.getAllUsersByProvider(provider);
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUsername(String username) {
        Optional<User> user = userRepository.getUserByUsername(username);
        if (user.isEmpty()) {
            return null;
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            return null;
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.getUserById(id);
        if (user.isEmpty()) {
            return null;
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto saveUser(UserDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userEntity = UserMapper.toEntity(user);
        User savedUser = userRepository.saveUser(userEntity);
        return UserMapper.toDto(savedUser);
    }
}
