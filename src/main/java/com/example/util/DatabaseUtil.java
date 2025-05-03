package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/filemanager";
    private static final String USER = "root";
    private static final String PASSWORD = "Sasha_307_1";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver не найден", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
