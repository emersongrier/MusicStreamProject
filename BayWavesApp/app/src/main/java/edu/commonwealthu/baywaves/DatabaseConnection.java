package edu.commonwealthu.baywaves;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:tcp://140.82.13.163:9092/./test;AUTOCOMMIT=OFF;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "wordculturesnailman";

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure H2 driver is loaded
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 driver not found", e);
        }
    }
}