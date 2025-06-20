package com.haiphamcoder.usermanagement.mapper;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.domain.entity.User.UserBuilder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User toEntity(UserDto userDto) {
        UserBuilder builder = User.builder();

        if (userDto.getId() != null && userDto.getId() > 0) {
            builder.id(userDto.getId());
        }
        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            builder.username(userDto.getUsername());
        }
        if (userDto.getFirstName() != null && !userDto.getFirstName().isEmpty()) {
            builder.firstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null && !userDto.getLastName().isEmpty()) {
            builder.lastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            builder.email(userDto.getEmail());
        }
        if (userDto.getProvider() != null && !userDto.getProvider().isEmpty()) {
            builder.provider(userDto.getProvider());
        }
        if (userDto.getProviderId() != null && !userDto.getProviderId().isEmpty()) {
            builder.providerId(userDto.getProviderId());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            builder.password(userDto.getPassword());
        }
        if (userDto.getAvatarUrl() != null && !userDto.getAvatarUrl().isEmpty()) {
            builder.avatarUrl(userDto.getAvatarUrl());
        }
        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            builder.role(userDto.getRole());
        }
        if (userDto.getCreatedAt() != null) {
            builder.createdAt(userDto.getCreatedAt());
        }
        if (userDto.getModifiedAt() != null) {
            builder.modifiedAt(userDto.getModifiedAt());
        }
        builder.emailVerified(userDto.isEmailVerified());
        builder.enabled(userDto.isEnabled());
        builder.firstLogin(userDto.isFirstLogin());

        return builder.build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .password(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .firstLogin(user.isFirstLogin())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    public static UserDto updateUser(User user, UserDto userDto) {
        UserDto result = toDto(user);
        if (userDto.getFirstName() != null && !userDto.getFirstName().isEmpty()) {
            result.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null && !userDto.getLastName().isEmpty()) {
            result.setLastName(userDto.getLastName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            result.setEmail(userDto.getEmail());
        }
        if (userDto.getAvatarUrl() != null && !userDto.getAvatarUrl().isEmpty()) {
            result.setAvatarUrl(userDto.getAvatarUrl());
        }
        return result;
    }

}
