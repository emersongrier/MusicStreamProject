package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;
import com.BayWave.Reset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserTable {
    /**
     * Register's a user's account, encrypting their password.
     */
    public static void register(Connection connection, String username, String password) throws SQLException {
        try {
            Reset.lock.lock();
            // ensure username is valid
            if (username.contains(" ")) {
                System.out.println("Username cannot contain whitespace, user not registered");
                return;
            }
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty");
                return;
            }
            // check if user exists
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM USER_ WHERE LOWER(usr_name)=LOWER(?)"); // case-insensitive
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) { // ResultSet is not empty, username unavailable
                System.out.println("Username already exists, user not registered");
                return;
            }
            ps = connection.prepareStatement(
                    "INSERT INTO USER_ (usr_name, usr_pass) VALUES (?, ?)");
            ps.setString(1, username);
            ps.setString(2, Reset.encoder.encode(password)); // encrypting password via bcrypt
            ps.executeUpdate();
            connection.commit();
            System.out.println("User registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns true if a given username exists within the database.
     */
    public static boolean usernameExists(Connection connection, String username) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            query.setString(1, username);
            ResultSet rs = query.executeQuery();
            //
            return rs.isBeforeFirst();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes a user, along with all of their associative entities.
     */
    public static void delete(Connection connection, String username) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, username);
            int results = ps.executeUpdate();
            if (results == 0) {
                System.out.println("User not deleted");
                return;
            }
            connection.commit();
            System.out.println("User deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the email address of a user.
     */
    public static void addEmail(Connection connection, String user, String email) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement rs = connection.prepareStatement(
                    "UPDATE USER_ SET usr_email=? WHERE LOWER(usr_name)=LOWER(?)");
            rs.setString(1, email);
            rs.setString(2, user);
            int updated = rs.executeUpdate();
            if (updated == 0) {
                System.out.println("Email not updated");
                return;
            }
            connection.commit();
            System.out.println("Email updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the phone number of a user.
     */
    public static void addPhone(Connection connection, String user, String phoneNumber) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement rs = connection.prepareStatement(
                    "UPDATE USER_ SET usr_phone=? WHERE LOWER(usr_name)=LOWER(?)");
            rs.setString(1, phoneNumber);
            rs.setString(2, user);
            int updated = rs.executeUpdate();
            if (updated == 0) {
                System.out.println("Phone number not updated");
                return;
            }
            connection.commit();
            System.out.println("Phone number updated");

        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the username of a user.
     */
    public static void updateUsername(Connection connection, String username, String newUsername) throws SQLException {
        try {
            Reset.lock.lock();
            // check if new username already exists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, newUsername);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Username already exists, not updated");
                return;
            }
            // username available
            ps = connection.prepareStatement(
                    "UPDATE USER_ SET usr_name=? WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, newUsername);
            ps.setString(2, username);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Username not updated");
                return;
            }
            connection.commit();
            System.out.println("Username updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the password of a user, encrypting it.
     */
    public static void updatePassword(Connection connection, String user, String newPassword) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("User not found");
                return;
            }
            ps = connection.prepareStatement("UPDATE USER_ SET usr_pass=? WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, Reset.encoder.encode(newPassword)); // encrypting
            ps.setString(2, user);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Password not updated");
                return;
            }
            connection.commit();
            System.out.println("Password updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Checks if the password matches a user's password.
     */
    public static boolean passwordValid(Connection connection, String user, String password) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("User not found");
                return false;
            }
            rs.next();
            return Reset.encoder.matches(password, rs.getString("usr_pass"));
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Sets a user's email to null.
     */
    public static void deleteEmail(Connection connection, String user) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement rs = connection.prepareStatement(
                    "UPDATE USER_ SET usr_email=NULL WHERE LOWER(usr_name)=LOWER(?)");
            rs.setString(1, user);
            int updated = rs.executeUpdate();
            if (updated == 0) {
                System.out.println("Email not deleted");
                return;
            }
            connection.commit();
            System.out.println("Email deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Sets a user's phone number to null.
     */
    public static void deletePhone(Connection connection, String user) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement rs = connection.prepareStatement(
                    "UPDATE USER_ SET usr_phone=NULL WHERE LOWER(usr_name)=LOWER(?)");
            rs.setString(1, user);
            int updated = rs.executeUpdate();
            if (updated == 0) {
                System.out.println("Phone number not deleted");
                return;
            }
            connection.commit();
            System.out.println("Phone number deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Prints the USER table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from USER_"); // select * from USERS_
        ResultSet rs = ps.executeQuery();
        System.out.println("USER_ TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the USER table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER_");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
