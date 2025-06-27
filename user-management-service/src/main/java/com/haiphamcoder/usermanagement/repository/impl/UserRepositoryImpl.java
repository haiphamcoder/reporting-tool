package com.haiphamcoder.usermanagement.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.usermanagement.domain.entity.User;
import com.haiphamcoder.usermanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND (u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findAllByNameOrEmailContainingIgnoreCaseAndIsDeletedFalse(@Param("search") String search,
            Pageable pageable);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByProvider(String provider);
}

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Page<User> getAllUsers(String search, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return userJpaRepository.findAllByNameOrEmailContainingIgnoreCaseAndIsDeletedFalse(search, pageable);
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
        return userJpaRepository.save(user);
    }
}
