package com.haiphamcoder.usermanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.proto.*;
import com.haiphamcoder.usermanagement.service.UserService;

import io.grpc.stub.StreamObserver;

public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserService userService;

    public UserServiceGrpcImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<GetAllUsersResponse> responseObserver) {
        List<UserDto> users = userService.getAllUsers();
        GetAllUsersResponse response = GetAllUsersResponse.newBuilder()
                .addAllUsers(users.stream()
                        .map(this::convertToUser)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllUsersByProvider(GetAllUsersByProviderRequest request,
            StreamObserver<GetAllUsersByProviderResponse> responseObserver) {
        List<UserDto> users = userService.getAllUsersByProvider(request.getProvider());
        GetAllUsersByProviderResponse response = GetAllUsersByProviderResponse.newBuilder()
                .addAllUsers(users.stream()
                        .map(this::convertToUser)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request,
            StreamObserver<GetUserByUsernameResponse> responseObserver) {
        UserDto user = userService.getUserByUsername(request.getUsername());
        GetUserByUsernameResponse response = GetUserByUsernameResponse.newBuilder()
                .setUser(this.convertToUser(user))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<GetUserByEmailResponse> responseObserver) {
        UserDto user = userService.getUserByEmail(request.getEmail());
        GetUserByEmailResponse response = GetUserByEmailResponse.newBuilder()
                .setUser(this.convertToUser(user))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        UserDto user = userService.getUserById(request.getId());
        GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                .setUser(this.convertToUser(user))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void saveUser(SaveUserRequest request, StreamObserver<SaveUserResponse> responseObserver) {
        UserDto userDto = this.convertToUserDto(request.getUser());
        UserDto savedUser = userService.saveUser(userDto);
        SaveUserResponse response = SaveUserResponse.newBuilder()
                .setUser(this.convertToUser(savedUser))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private User convertToUser(UserDto userDto) {
        return User.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setEmail(userDto.getEmail())
                .setAvatarUrl(userDto.getAvatarUrl())
                .build();
    }

}
