package com.BayWave;

import java.sql.*;
import java.util.concurrent.locks.ReentrantLock;

import io.liquer.pencil.encoder.SSHAPasswordEncoder;

public class Reset {
    public static final SSHAPasswordEncoder encoder = new SSHAPasswordEncoder();
    public static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;INIT=RUNSCRIPT FROM 'classpath:BayWave.sql';AUTOCOMMIT=OFF;")) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("Database reset successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
