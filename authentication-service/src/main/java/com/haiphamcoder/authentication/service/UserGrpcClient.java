package com.haiphamcoder.authentication.service;

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
        User user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto getUserById(String id) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder().setId(Long.parseLong(id)).build();
        GetUserByIdResponse response = userServiceBlockingStub.getUserById(request);
        User user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto getUserByUsername(String username) {
        GetUserByUsernameRequest request = GetUserByUsernameRequest.newBuilder().setUsername(username).build();
        GetUserByUsernameResponse response = userServiceBlockingStub.getUserByUsername(request);
        User user = response.getUser();
        return convertUserToUserDto(user);
    }

    public UserDto saveUser(User user) {
        SaveUserRequest request = SaveUserRequest.newBuilder().setUser(user).build();
        SaveUserResponse response = userServiceBlockingStub.saveUser(request);
        UserDto userDto = convertUserToUserDto(response.getUser());
        return userDto;
    }

    private UserDto convertUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .provider(user.getProvider())
                .build();
    }
}
