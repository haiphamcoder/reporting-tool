package com.haiphamcoder.usermanagement.mapper;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .emailVerified(userDto.isEmailVerified())
                .provider(userDto.getProvider())
                .providerId(userDto.getProviderId())
                .password(userDto.getPassword())
                .avatarUrl(userDto.getAvatarUrl())
                .role(userDto.getRole())
                .enabled(userDto.isEnabled())
                .createdAt(userDto.getCreatedAt())
                .modifiedAt(userDto.getModifiedAt())
                .build();
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
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

}
