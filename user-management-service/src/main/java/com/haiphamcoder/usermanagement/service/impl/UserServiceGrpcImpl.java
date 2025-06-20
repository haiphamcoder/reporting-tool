package com.haiphamcoder.usermanagement.service.impl;

import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.usermanagement.proto.*;
import com.haiphamcoder.usermanagement.service.UserService;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserService userService;

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request,
            StreamObserver<GetUserByUsernameResponse> responseObserver) {
        try {
            UserDto user = userService.getUserByUsername(request.getUsername());
            GetUserByUsernameResponse response = GetUserByUsernameResponse.newBuilder()
                    .setUser(this.convertToUser(user))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage())));
        }
    }

    @Override
    public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<GetUserByEmailResponse> responseObserver) {
        try {
            UserDto user = userService.getUserByEmail(request.getEmail());
            GetUserByEmailResponse response = GetUserByEmailResponse.newBuilder()
                    .setUser(this.convertToUser(user))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage())));
        }
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        try {
            UserDto user = userService.getUserById(request.getId());
            GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                    .setUser(this.convertToUser(user))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage())));
        }
    }

    @Override
    public void saveUser(SaveUserRequest request, StreamObserver<SaveUserResponse> responseObserver) {
        UserDto userDto = this.convertToUserDto(request.getUser());

        UserDto savedUser = null;
        UserDto existingUser = null;
        try {
            existingUser = userService.getUserByEmail(userDto.getEmail());
        } catch (ResourceNotFoundException e) {
            existingUser = null;
        }

        if (existingUser != null) {
            savedUser = userService.updateUser(userDto);
        } else {
            savedUser = userService.createUser(userDto);
        }

        SaveUserResponse response = SaveUserResponse.newBuilder()
                .setUser(this.convertToUser(savedUser))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private UserDto convertToUserDto(UserProto user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setEmailVerified(user.getEmailVerified());
        userDto.setProvider(user.getProvider());
        userDto.setProviderId(user.getProviderId());
        userDto.setAvatarUrl(user.getAvatarUrl());
        userDto.setFirstLogin(user.getFirstLogin());
        userDto.setRole(user.getRole());
        return userDto;
    }

    private UserProto convertToUser(UserDto userDto) {
        return UserProto.newBuilder()
                .setId(userDto.getId())
                .setUsername(userDto.getUsername())
                .setPassword(userDto.getPassword())
                .setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : "")
                .setLastName(userDto.getLastName() != null ? userDto.getLastName() : "")
                .setEmail(userDto.getEmail())
                .setEmailVerified(userDto.isEmailVerified())
                .setProvider(userDto.getProvider())
                .setProviderId(userDto.getProviderId() != null ? userDto.getProviderId() : "")
                .setAvatarUrl(userDto.getAvatarUrl() != null ? userDto.getAvatarUrl() : "")
                .setFirstLogin(userDto.isFirstLogin())
                .setRole(userDto.getRole())
                .setEnabled(userDto.isEnabled())
                .setCreatedAt(userDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setModifiedAt(userDto.getModifiedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

}
