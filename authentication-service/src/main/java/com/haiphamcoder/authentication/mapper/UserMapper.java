package com.haiphamcoder.authentication.mapper;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.shared.StringUtils;
import com.haiphamcoder.usermanagement.proto.UserProto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserProto toUser(UserDto userDto) {
        UserProto.Builder builder = UserProto.newBuilder();
        if (userDto.getId() != null) {
            builder.setId(userDto.getId());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getUsername())) {
            builder.setUsername(userDto.getUsername());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getEmail())) {
            builder.setEmail(userDto.getEmail());
        }
        if (userDto.getEmailVerified() != null) {
            builder.setEmailVerified(userDto.getEmailVerified());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getFirstName())) {
            builder.setFirstName(userDto.getFirstName());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getLastName())) {
            builder.setLastName(userDto.getLastName());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getProvider())) {
            builder.setProvider(userDto.getProvider());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getProviderId())) {
            builder.setProviderId(userDto.getProviderId());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getAvatarUrl())) {
            builder.setAvatarUrl(userDto.getAvatarUrl());
        }
        if (!StringUtils.isNullOrEmpty(userDto.getRole())) {
            builder.setRole(userDto.getRole());
        }
        return builder.build();
    }

}
