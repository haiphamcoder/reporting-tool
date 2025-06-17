package com.haiphamcoder.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.haiphamcoder.usermanagement.domain.entity.User;

public interface UserRepository {
    Page<User> getAllUsers(Long userId, Integer page, Integer limit);

    List<User> getAllUsersByProvider(String provider);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    User saveUser(User user);
}
