package com.haiphamcoder.authentication.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.service.UserGrpcClient;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserService {

    private final UserGrpcClient userGrpcClient;

    public UserDto getUserByEmail(String email) {
        return userGrpcClient.getUserByEmail(email);
    }

}
