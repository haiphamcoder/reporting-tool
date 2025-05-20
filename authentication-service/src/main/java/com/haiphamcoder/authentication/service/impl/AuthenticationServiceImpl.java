package com.haiphamcoder.authentication.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.haiphamcoder.authentication.domain.dto.UserDto;
import com.haiphamcoder.authentication.domain.entity.RefreshToken;
import com.haiphamcoder.authentication.domain.model.AuthenticationRequest;
import com.haiphamcoder.authentication.domain.model.CustomUserDetail;
import com.haiphamcoder.authentication.domain.model.RegisterRequest;
import com.haiphamcoder.authentication.domain.model.TokenType;
import com.haiphamcoder.authentication.security.JwtTokenProvider;
import com.haiphamcoder.authentication.security.exception.UsernameOrPasswordIncorrectException;
import com.haiphamcoder.authentication.service.AuthenticationService;
import com.haiphamcoder.authentication.service.RefreshTokenService;
import com.haiphamcoder.authentication.service.UserGrpcClient;
import com.haiphamcoder.authentication.shared.CookieUtils;
import com.haiphamcoder.authentication.shared.DateTimeUtils;
import com.haiphamcoder.authentication.shared.SnowflakeIdGenerator;
import com.haiphamcoder.authentication.shared.StringUtils;
import com.haiphamcoder.authentication.shared.http.RestAPIResponse;
import com.haiphamcoder.usermanagement.proto.User;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

        private final UserGrpcClient userGrpcClient;
        private final RefreshTokenService refreshTokenService;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

        private static final String COOKIE_USER_ID = "user-id";
        private static final String COOKIE_ACCESS_TOKEN = "access-token";
        private static final String COOKIE_REFRESH_TOKEN = "refresh-token";

        @Override
        public UserDto register(RegisterRequest request) {
                User user = User.newBuilder()
                                .setFirstName(request.getFirstName())
                                .setLastName(request.getLastName())
                                .setUsername(request.getUsername())
                                .setPassword(request.getPassword())
                                .setEmail(request.getEmail())
                                .setRole(request.getRole().getName())
                                .build();
                return userGrpcClient.saveUser(user);
        }

        @Override
        public boolean checkUsernameExisted(String username) {
                return userGrpcClient.getUserByUsername(username) != null;
        }

        @Override
        public boolean checkEmailExisted(String email) {
                return userGrpcClient.getUserByEmail(email) != null;
        }

        @Override
        public RestAPIResponse<String> authenticate(AuthenticationRequest request, HttpServletResponse response) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));
                        CustomUserDetail authenticatedUser = (CustomUserDetail) authentication.getPrincipal();
                        String refreshToken = jwtTokenProvider.generateRefreshToken(authenticatedUser.getUsername());
                        RefreshToken refreshTokenEntity = RefreshToken.builder()
                                        .id(snowflakeIdGenerator.generateId())
                                        .userId(authenticatedUser.getId())
                                        .tokenValue(refreshToken)
                                        .tokenType(TokenType.BEARER)
                                        .expiredAt(DateTimeUtils.convertToLocalDateTime(
                                                        jwtTokenProvider.extractExpiration(refreshToken)))
                                        .build();
                        RefreshToken savedRefreshToken = refreshTokenService.saveUserToken(refreshTokenEntity);
                        if (savedRefreshToken == null) {
                                throw new RuntimeException("Failed to save refresh token");
                        }
                        String accessTokenValue = jwtTokenProvider.generateAccessToken(authenticatedUser.getUsername());

                        CookieUtils.addCookie(response, COOKIE_USER_ID, String.valueOf(authenticatedUser.getId()));
                        CookieUtils.addCookie(response, COOKIE_ACCESS_TOKEN, accessTokenValue);
                        CookieUtils.addCookie(response, COOKIE_REFRESH_TOKEN, savedRefreshToken.getTokenValue());

                        return RestAPIResponse.ResponseFactory.createResponse("Authentication successful");

                } catch (AuthenticationException e) {
                        throw new UsernameOrPasswordIncorrectException();
                }
        }

        @Override
        public String refreshToken(String authHeader) {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String refreshToken = authHeader.substring(7);
                        RefreshToken existedRefreshToken = refreshTokenService.getTokenByValue(refreshToken);
                        if (existedRefreshToken != null) {
                                String accessToken = jwtTokenProvider
                                                .generateAccessToken(existedRefreshToken.getUserId().toString());
                                if (!StringUtils.isNullOrEmpty(accessToken)) {
                                        return accessToken;
                                }
                        }
                }
                return null;
        }

}
