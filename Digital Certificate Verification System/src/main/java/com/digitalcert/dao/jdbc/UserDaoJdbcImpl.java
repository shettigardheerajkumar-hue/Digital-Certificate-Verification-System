package com.digitalcert.dao.jdbc;

import com.digitalcert.dao.UserDao;
import com.digitalcert.model.User;
import com.digitalcert.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDaoJdbcImpl implements UserDao {

    private final DBConnectionUtil dbConnectionUtil;

    public UserDaoJdbcImpl(DBConnectionUtil dbConnectionUtil) {
        this.dbConnectionUtil = dbConnectionUtil;
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (username, password_hash, created_at)
                VALUES (?, ?, ?)
                """;

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setTimestamp(3, Timestamp.valueOf(now));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Creating user failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                    user.setCreatedAt(now);
                    return user;
                } else {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + e.getMessage(), e);
        }
    }
}

