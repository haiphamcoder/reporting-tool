package com.haiphamcoder.cdp.domain.repository;

import java.util.Optional;

import com.haiphamcoder.cdp.domain.entity.User;

public interface UserRepository {
    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    User saveUser(User user);
}
