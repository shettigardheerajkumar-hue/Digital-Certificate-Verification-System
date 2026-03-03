package com.digitalcert.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class DBConnectionUtil {

    private final String url;
    private final String username;
    private final String password;

    public DBConnectionUtil() {
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(
                    DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties"),
                    "db.properties not found in classpath"
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration from db.properties", e);
        }

        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");

        if (url == null || username == null || password == null) {
            throw new IllegalStateException("Database configuration properties db.url, db.username, db.password must be set");
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}

