package com.haiphamcoder.authentication.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.proto.*;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserGrpcClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    
    public UserGrpcClient(@Qualifier("userManagementServiceChannel") ManagedChannel userManagementServiceChannel) {
        this.userServiceBlockingStub = UserServiceGrpc.newBlockingStub(userManagementServiceChannel);
    }

    public UserDto getUserByEmail(String email) {
        GetUserByEmailRequest request = GetUserByEmailRequest.newBuilder().setEmail(email).build();
        GetUserByEmailResponse response = userServiceBlockingStub.getUserByEmail(request);
        User user = response.getUser();
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                
                .build();
    }
}
