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
                .map(user -> User.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setAvatarUrl(user.getAvatarUrl())
                    .build())
                .collect(Collectors.toList()))
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    
}
