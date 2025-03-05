package com.haiphamcoder.cdp.adapter.dto.mapper;

import com.haiphamcoder.cdp.adapter.dto.UserDto;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.Role;

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
                .password(userDto.getPassword())
                .avatarUrl(userDto.getAvatarUrl())
                .role(Role.valueOf(userDto.getRole()))
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
    
}
