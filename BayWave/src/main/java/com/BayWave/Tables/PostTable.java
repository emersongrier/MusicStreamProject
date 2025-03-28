package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostTable {
    /**
     * Prints the POST table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM POST");
        ResultSet rs = ps.executeQuery();
        System.out.println("POST TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String user, String text) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO POST (usr_id, pst_text) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setString(2, text);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Post not added");
            }
            connection.commit();
            System.out.println("Post added");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void reply(Connection connection, String user, String text, int postId) throws SQLException {
        try {
            Reset.lock.lock();
            if (!TableUtil.isValidPost(connection, postId)) {
                System.err.println("Post not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO POST (usr_id, pst_text, repl_pst_id) VALUES (?, ?, ?)");
            ps.setInt(1, userId);
            ps.setString(2, text);
            ps.setInt(3, postId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Post not added");
            }
            connection.commit();
            System.out.println("Post added");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, int postId) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM POST WHERE pst_id=?");
            ps.setInt(1, postId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Post not deleted");
                return;
            }
            connection.commit();
            System.out.println("Post deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns a string representing the specified row in the POST table,
     * which contains the following attributes in order starting from index 0:
     * pst_id, usr_id, pst_text, pst_likes, pst_repls, repl_pst_id.
     */
    public static ArrayList<String[]> getPostsForUser(Connection connection, String user) throws SQLException {
        int userId = TableUtil.getUserID(connection, user);
        if (userId == -1) {
            System.err.println("User not found");
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM POST WHERE usr_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Posts not found");
            return null;
        }
        return TableUtil.getTable(rs);
    }

    /**
     * Updates the description of a playlist.
     */
    public static void edit(Connection connection, int postId, String newText) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE POST SET pst_text=? WHERE pst_id=?");
            ps.setString(1, newText);
            ps.setInt(2, postId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Post not edited");
                return;
            }
            connection.commit();
            System.out.println("Post edited");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the POST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM POST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
