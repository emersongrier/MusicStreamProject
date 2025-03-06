/**
 * Helper functions for interacting with the LIKE_TRACK table, which is an associative entity
 * linking users to tracks, indicating that the user added the track to their likes.
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

public class LikeTrackTable {
    /**
     * Prints the LIKE_TRACK table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from LIKE_TRACK");
        ResultSet rs = ps.executeQuery();
        System.out.println("LIKE_TRACK TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds track to user's likes.
     */
    public static void register(Connection connection, String user, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.out.println("Track not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // check if user already liked track
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_TRACK WHERE usr_id=? AND trk_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, trackId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("User already liked track");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO LIKE_TRACK (usr_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track like not registered");
                return;
            }
            connection.commit();
            System.out.println("Track like registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a track from a user's likes.
     */
    public static void delete(Connection connection, String user, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.out.println("Track not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM LIKE_TRACK WHERE usr_id=? AND trk_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track like not deleted");
                return;
            }
            connection.commit();
            System.out.println("Track like deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns true if user has that track in their likes.
     */
    public static Boolean contains(Connection connection, String user, String artist, String album, String track) throws SQLException {
        int userId = TableUtil.getUserID(connection, user);
        if (userId == -1) {
            System.out.println("User not found");
            return false;
        }
        int trackId = TableUtil.getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            System.out.println("Track not found");
            return false;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_TRACK WHERE usr_id=? AND trk_id=?");
        ps.setInt(1, userId);
        ps.setInt(2, trackId);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the LIKE_TRACK table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
