package com.haiphamcoder.authentication.service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.domain.model.AuthenticationRequest;
import com.haiphamcoder.authentication.domain.model.RegisterRequest;
import com.haiphamcoder.authentication.shared.http.RestAPIResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

        public UserDto register(RegisterRequest request);

        public boolean checkUsernameExisted(String username);

        public boolean checkEmailExisted(String email);

        public RestAPIResponse<String> authenticate(AuthenticationRequest request, HttpServletResponse response);

        public String refreshToken(String authHeader);

        public boolean createAdminUser();

}
