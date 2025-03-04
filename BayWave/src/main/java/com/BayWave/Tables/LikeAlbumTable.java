package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LikeAlbumTable {
    /**
     * Prints the LIKE_ALBUM table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from LIKE_ALBUM");
        ResultSet rs = ps.executeQuery();
        System.out.println("LIKE_ALBUM TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds album to user's likes.
     */
    public static void register(Connection connection, String user, String artist, String album) throws SQLException {
        try {
            Reset.lock.lock();
            int albumId = TableUtil.getAlbumID(connection, artist, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // check if user already liked album
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_ALBUM WHERE usr_id=? AND alb_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, albumId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("User already liked album");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO LIKE_ALBUM (usr_id, alb_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, albumId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Album like not registered");
                return;
            }
            connection.commit();
            System.out.println("Album like registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes an album from a user's likes.
     */
    public static void delete(Connection connection, String user, String artist, String album) throws SQLException {
        try {
            Reset.lock.lock();
            int albumId = TableUtil.getAlbumID(connection, artist, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM LIKE_ALBUM WHERE usr_id=? AND alb_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, albumId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Album like not deleted");
                return;
            }
            connection.commit();
            System.out.println("Album like deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the LIKE_ALBUM table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_ALBUM");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
