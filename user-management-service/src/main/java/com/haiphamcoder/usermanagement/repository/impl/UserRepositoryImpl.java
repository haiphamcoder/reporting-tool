package com.haiphamcoder.usermanagement.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.repository.UserRepository;
import com.haiphamcoder.usermanagement.shared.SnowflakeIdGenerator;

import lombok.RequiredArgsConstructor;

@Repository
interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByProvider(String provider);
}

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

    @Override
    public List<User> getAllUsers() {
        return userJpaRepository.findAll();
    }

    @Override
    public List<User> getAllUsersByProvider(String provider) {
        return userJpaRepository.findAllByProvider(provider);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        user.setId(snowflakeIdGenerator.generateId());
        Optional<User> existingUser = userJpaRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return userJpaRepository.save(existingUser.get());
        }
        return userJpaRepository.save(user);
    }
}
