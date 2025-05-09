package com.haiphamcoder.cdp.application.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.UserService;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public List<User> getAllUsersByProvider(String provider) {
        return userRepository.getAllUsersByProvider(provider);
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.getUserByUsername(username);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.getUserById(id);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveUser(user);
    }
}
