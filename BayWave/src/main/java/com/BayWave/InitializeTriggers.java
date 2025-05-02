package com.BayWave;

import java.sql.*;

public class InitializeTriggers {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;INIT=RUNSCRIPT FROM 'classpath:InitializeTriggers.sql';AUTOCOMMIT=OFF;")) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("InitializeTriggers.sql was run successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
