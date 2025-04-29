package com.haiphamcoder.cdp.domain.repository;

import java.util.List;
import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.User;

public interface UserRepository {
    List<User> getAllUsers();

    List<User> getAllUsersByProvider(String provider);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    User saveUser(User user);
}
