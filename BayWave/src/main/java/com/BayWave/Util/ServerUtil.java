package com.BayWave.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerUtil {
    /**
     * Replace this method with your actual database connection logic.
     */
    public static Connection getConnection() throws SQLException {
        // Example using DriverManager.
        // Update the URL, username, and password according to your DB setup.
        String url = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        return DriverManager.getConnection(url);
    }
}
