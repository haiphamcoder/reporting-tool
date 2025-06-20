package com.haiphamcoder.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haiphamcoder.usermanagement.service.UserService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.usermanagement.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordRequest;
import com.haiphamcoder.usermanagement.mapper.UserMapper;
import com.haiphamcoder.usermanagement.repository.UserRepository;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        autoRegisterAdminAccount();
    }

    @Override
    public Page<UserDto> getAllUsers(Long userId, Integer page, Integer limit) {
        Page<User> users = userRepository.getAllUsers(userId, page, limit);
        return users.map(UserMapper::toDto);
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
            throw new ResourceNotFoundException("User", username);
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", email);
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.getUserById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", id);
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

    private void autoRegisterAdminAccount() {
        final String adminUsername = "admin";
        final String adminEmail = "admin@reporting-tool.com";
        final String defaultPassword = "admin";
        Optional<User> existing = userRepository.getUserByUsername(adminUsername);
        if (existing.isEmpty()) {
            UserDto adminUser = UserDto.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(defaultPassword)
                    .firstName("Admin")
                    .lastName("User")
                    .role("admin")
                    .firstLogin(true)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            saveUser(adminUser);
        }
    }

    @Override
    public UserDto changePassword(Long userId, Long targetUserId, ChangePasswordRequest request) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!user.get().getRole().equals("admin") || !user.get().getId().equals(targetUserId)) {
            throw new ForbiddenException("You are not allowed to change password for this user");
        }
        Optional<User> targetUser = userRepository.getUserById(targetUserId);
        if (targetUser.isEmpty()) {
            throw new ResourceNotFoundException("User", targetUserId);
        }
        targetUser.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
        targetUser.get().setFirstLogin(false);
        userRepository.saveUser(targetUser.get());
        return UserMapper.toDto(targetUser.get());
    }
}
