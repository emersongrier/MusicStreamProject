/**
 * Helper functions for interacting with the TRACK_LISTEN table, which is an associative entity
 * linking users to tracks for the purposes of recording their statistics, keeping track of
 * each individual instance of them listening to a particular track, and on what date.
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

public class TrackListenTable {
    /**
     * Prints the TRACK_LISTEN table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from TRACK_LISTEN");
        ResultSet rs = ps.executeQuery();
        System.out.println("TRACK_LISTEN TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a single listen for the specified track to a user's statistics.
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
            // listen can already exist, no need to check
            PreparedStatement ps = connection.prepareStatement("INSERT INTO TRACK_LISTEN (usr_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track like not added for user");
                return;
            }
            connection.commit();
            System.out.println("Track like added for user");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes the first TRACK_LISTEN that appears in the ResultSet given a specified user, track, and date.
     */
    public static void deleteOne(Connection connection, String user, String artist, String album, String track, String date) throws SQLException {
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_LISTEN WHERE usr_id=? AND trk_id=? AND trk_lst_date=?");
            ps.setInt(1, userId);
            ps.setInt(2, trackId);
            ps.setString(3, date);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No listens found");
                return;
            }
            rs.next();
            int trackListenId = rs.getInt(1);
            ps = connection.prepareStatement("DELETE FROM TRACK_LISTEN WHERE trk_lst_id=?");
            ps.setInt(1, trackListenId);
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
     * Returns an ArrayList of String tables. Each string table represents a row in the TRACK_LISTEN table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_LISTEN");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
