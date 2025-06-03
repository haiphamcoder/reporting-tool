package com.haiphamcoder.authentication.service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.domain.model.AuthenticationRequest;
import com.haiphamcoder.authentication.domain.model.RegisterRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

        public UserDto register(RegisterRequest request);

        public boolean checkUsernameExisted(String username);

        public boolean checkEmailExisted(String email);

        public boolean authenticate(AuthenticationRequest request, HttpServletResponse response);

        public String refreshToken(String authHeader);

}
