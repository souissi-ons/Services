package com.university.archives.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        // Fallback local
        if (dbUrl == null) dbUrl = "jdbc:postgresql://localhost:5432/archives_db";
        if (dbUser == null) dbUser = "postgres";
        if (dbPassword == null) dbPassword = "ons";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}