/**
 * Helper functions for interacting with the LIKE_POST table, which is an associative entity
 * linking users to post, indicating that the user added the post to their likes.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LikePostTable {
    /**
     * Prints the LIKE_TRACK table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from LIKE_POST");
        ResultSet rs = ps.executeQuery();
        System.out.println("LIKE_POST TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String username, int postId) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, username);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            // check if user already liked post
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_POST WHERE usr_id=? AND pst_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.err.println("Like already exists");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO LIKE_POST (usr_id, pst_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Like not added");
                return;
            }
            // increment like count
            ps = connection.prepareStatement("SELECT pst_likes FROM POST WHERE pst_id=?");
            ps.setInt(1, postId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Post not found");
                return;
            }
            rs.next();
            int numLikes = rs.getInt("pst_likes");
            ps = connection.prepareStatement("UPDATE POST SET pst_likes=? WHERE pst_id=?");
            ps.setInt(1, numLikes + 1);
            ps.setInt(2, postId);
            connection.commit();
            System.out.println("Added post to user's likes");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, String username, int postId) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, username);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM LIKE_POST WHERE usr_id=? AND pst_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Like not deleted");
                return;
            }
            connection.commit();
            System.out.println("Removed post from user's likes");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the LIKE_POST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_POST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
