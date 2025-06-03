package com.haiphamcoder.authentication.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.proto.*;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserGrpcClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserGrpcClient(ManagedChannel userManagementServiceChannel) {
        this.userServiceBlockingStub = UserServiceGrpc.newBlockingStub(userManagementServiceChannel);
    }

    public UserDto getUserByEmail(String email) {
        GetUserByEmailRequest request = GetUserByEmailRequest.newBuilder().setEmail(email).build();
        GetUserByEmailResponse response = userServiceBlockingStub.getUserByEmail(request);
        UserProto user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto getUserById(Long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder().setId(userId).build();
        GetUserByIdResponse response = userServiceBlockingStub.getUserById(request);
        UserProto user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto getUserByUsername(String username) {
        GetUserByUsernameRequest request = GetUserByUsernameRequest.newBuilder().setUsername(username).build();
        GetUserByUsernameResponse response = userServiceBlockingStub.getUserByUsername(request);
        UserProto user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto saveUser(UserProto user) {
        SaveUserRequest request = SaveUserRequest.newBuilder().setUser(user).build();
        SaveUserResponse response = userServiceBlockingStub.saveUser(request);
        UserDto userDto = convertUserToUserDto(response.getUser());
        return userDto;
    }

    private UserDto convertUserToUserDto(UserProto user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.getEnabled())
                .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreatedAt()), ZoneId.systemDefault()))
                .modifiedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getModifiedAt()), ZoneId.systemDefault()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .provider(user.getProvider())
                .build();
    }
}
