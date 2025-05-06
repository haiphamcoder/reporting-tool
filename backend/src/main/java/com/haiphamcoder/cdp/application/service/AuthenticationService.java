package com.haiphamcoder.cdp.application.service;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;
import com.haiphamcoder.cdp.domain.model.RegisterRequest;
import com.haiphamcoder.cdp.shared.http.RestAPIResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

        public User register(RegisterRequest request);

        public boolean checkUsernameExisted(String username);

        public boolean checkEmailExisted(String email);

        public RestAPIResponse<String> authenticate(AuthenticationRequest request, HttpServletResponse response);

        public String refreshToken(String authHeader);

        public boolean createAdminUser();

}
