package com.BayWave;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;")) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("Database connected successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
