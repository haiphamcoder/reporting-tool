package com.haiphamcoder.cdp.application.service;

import java.util.List;
import com.haiphamcoder.cdp.domain.entity.User;

public interface UserService {

    public List<User> getAllUsers();

    public List<User> getAllUsersByProvider(String provider);

    public User getUserByUsername(String username);

    public User getUserByEmail(String email);

    public User getUserById(Long id);

    public User saveUser(User user);
}
