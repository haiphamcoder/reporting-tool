package com.haiphamcoder.cdp.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        Optional<User> existingUser = userJpaRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return userJpaRepository.save(existingUser.get());
        }
        return userJpaRepository.save(user);
    }
}
