package com.haiphamcoder.reporting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.haiphamcoder.reporting.application.service.impl.UserServiceImpl;
import com.haiphamcoder.reporting.domain.entity.User;
import com.haiphamcoder.reporting.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .email("testuser@example.com")
                .build();
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUser() {
        // Arrange
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("testuser@example.com", result.getEmail());
        verify(userRepository).getUserById(1L);
    }
}
