package com.haiphamcoder.authentication.mapper;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.proto.UserProto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserProto toUser(UserDto userDto) {
        return UserProto.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setProvider(userDto.getProvider())
                .setProviderId(userDto.getProviderId())
                .setEmailVerified(userDto.getEmailVerified())
                .setRole(userDto.getRole())
                .setAvatarUrl(userDto.getAvatarUrl())
                .build();
    }
    
}
