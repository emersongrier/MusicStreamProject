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
        DriverManager.registerDriver(new org.h2.Driver());
        String url = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        return DriverManager.getConnection(url);
    }
}
