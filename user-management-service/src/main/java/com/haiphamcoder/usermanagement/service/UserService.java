package com.haiphamcoder.usermanagement.service;

import java.util.List;

import com.haiphamcoder.usermanagement.domain.dto.UserDto;
import com.haiphamcoder.usermanagement.domain.model.ChangePasswordRequest;
import com.haiphamcoder.usermanagement.domain.model.ChangeRoleRequest;
import com.haiphamcoder.usermanagement.domain.model.Metadata;
import com.haiphamcoder.usermanagement.shared.Pair;

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

}
