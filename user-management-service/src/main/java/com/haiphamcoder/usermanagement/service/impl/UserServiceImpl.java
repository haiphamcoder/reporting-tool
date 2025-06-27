package com.haiphamcoder.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haiphamcoder.usermanagement.service.UserService;
import com.haiphamcoder.usermanagement.shared.Pair;
import com.haiphamcoder.usermanagement.shared.SnowflakeIdGenerator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.usermanagement.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordRequest;
import com.haiphamcoder.usermanagement.domain.model.ChangeRoleRequest;
import com.haiphamcoder.usermanagement.domain.model.Metadata;
import com.haiphamcoder.usermanagement.mapper.UserMapper;
import com.haiphamcoder.usermanagement.repository.UserRepository;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@reporting-tool.com";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final String ADMIN_ROLE = "admin";

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
    public Pair<List<UserDto>, Metadata> getAllUsers(Long userId, String search, Integer page, Integer limit) {

        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!user.get().getRole().equals(ADMIN_ROLE)) {
            throw new ForbiddenException("You are not allowed to get all users");
        }

        Page<User> users = userRepository.getAllUsers(search, page, limit);
        
        return new Pair<>(users.map(UserMapper::toDto).toList(), Metadata.builder()
                .numberOfElements(users.getNumberOfElements())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .currentPage(users.getNumber())
                .pageSize(users.getSize())
                .build());
    }

    @Override
    public List<UserDto> getAllUsersByProvider(String provider) {
        List<User> users = userRepository.getAllUsersByProvider(provider);
        return users.stream().map(UserMapper::toDto).toList();
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
    public UserDto createUser(UserDto user) {
        user.setId(SnowflakeIdGenerator.getInstance().generateId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userEntity = UserMapper.toEntity(user);
        User savedUser = userRepository.saveUser(userEntity);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto user) {
        Optional<User> existingUser = userRepository.getUserById(user.getId());
        if (existingUser.isEmpty()) {
            throw new ResourceNotFoundException("User", user.getId());
        }
        UserDto updatedUser = UserMapper.updateUser(existingUser.get(), user);
        User savedUser = userRepository.saveUser(UserMapper.toEntity(updatedUser));
        return UserMapper.toDto(savedUser);
    }

    private void autoRegisterAdminAccount() {
        Optional<User> existing = userRepository.getUserByUsername(ADMIN_USERNAME);
        if (existing.isEmpty()) {
            UserDto adminUser = UserDto.builder()
                    .username(ADMIN_USERNAME)
                    .email(ADMIN_EMAIL)
                    .password(DEFAULT_PASSWORD)
                    .firstName("Admin")
                    .lastName("User")
                    .role(ADMIN_ROLE)
                    .firstLogin(true)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            createUser(adminUser);
        }
    }

    @Override
    public UserDto changePassword(Long userId, Long targetUserId, ChangePasswordRequest request) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!user.get().getRole().equals(ADMIN_ROLE) || !user.get().getId().equals(targetUserId)) {
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

    @Override
    public UserDto changeRole(Long userId, Long targetUserId, ChangeRoleRequest request) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!user.get().getRole().equals(ADMIN_ROLE)) {
            throw new ForbiddenException("You are not allowed to change role for this user");
        }
        if (user.get().getId().equals(targetUserId) && !request.getRole().equals(ADMIN_ROLE)) {
            throw new ForbiddenException(
                    "You are not allowed to change role for yourself. You will lose your admin privileges.");
        }
        Optional<User> targetUser = userRepository.getUserById(targetUserId);
        if (targetUser.isEmpty()) {
            throw new ResourceNotFoundException("User", targetUserId);
        }
        targetUser.get().setRole(request.getRole());
        userRepository.saveUser(targetUser.get());
        return UserMapper.toDto(targetUser.get());
    }

    @Override
    public void deleteUser(Long userId, Long targetUserId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        if (!user.get().getRole().equals(ADMIN_ROLE)) {
            throw new ForbiddenException("You are not allowed to delete this user");
        }
        if (user.get().getId().equals(targetUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete yourself. You will lose your admin privileges.");
        }
        Optional<User> targetUser = userRepository.getUserById(targetUserId);
        if (targetUser.isEmpty()) {
            throw new ResourceNotFoundException("User", targetUserId);
        }
        targetUser.get().setDeleted(true);
        userRepository.saveUser(targetUser.get());
    }

}
