package com.digitalcert.dao;

import com.digitalcert.model.User;

import java.util.Optional;

public interface UserDao {

    User save(User user);

    Optional<User> findByUsername(String username);
}

