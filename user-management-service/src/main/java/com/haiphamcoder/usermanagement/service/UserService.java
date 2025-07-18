package com.haiphamcoder.usermanagement.service;

import java.util.List;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordRequest;
import com.haiphamcoder.usermanagement.domain.model.ChangeRoleRequest;
import com.haiphamcoder.usermanagement.domain.model.Metadata;
import com.haiphamcoder.usermanagement.shared.Pair;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    public Pair<List<UserDto>, Metadata> getAllUsers(Long userId, String search, Integer page, Integer limit);

    public List<UserDto> getAllUsersByProvider(String provider);

    public UserDto getUserByUsername(String username);

    public UserDto getUserByEmail(String email);

    public UserDto getUserById(Long id);

    public UserDto createUser(UserDto user);

    public UserDto updateUser(UserDto user);

    public UserDto changePassword(Long userId, Long targetUserId, ChangePasswordRequest request);

    public UserDto changeRole(Long userId, Long targetUserId, ChangeRoleRequest request);

    public void deleteUser(Long userId, Long targetUserId);

    public void forgotPassword(String email);

    public void verifyOtp(String otp, String email, HttpServletResponse response);

    public String checkProvider(String email);

    public void resetPassword(Long userId, String email, String password);

}
