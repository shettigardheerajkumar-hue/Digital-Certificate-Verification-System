package com.digitalcert.service;

import com.digitalcert.dao.UserDao;
import com.digitalcert.model.User;
import com.digitalcert.util.PasswordUtil;

import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        Optional<User> existing = userDao.findByUsername(username);
        if (existing.isPresent()) {
            throw new RuntimeException("Username already exists. Please choose another one.");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hashPassword(password));

        return userDao.save(user);
    }

    public boolean authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        Optional<User> userOpt = userDao.findByUsername(username.trim());
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        String passwordHash = PasswordUtil.hashPassword(password);
        return passwordHash.equals(user.getPasswordHash());
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
    }
}

