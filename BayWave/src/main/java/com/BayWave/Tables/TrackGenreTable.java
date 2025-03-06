/**
 * Helper functions for interacting with the TRACK_GENRE table, which is an associative entity
 * linking genres to tracks, indicating that the track has that genre.
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

public class TrackGenreTable {
    /**
     * Prints the TRACK_GENRE table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from TRACK_GENRE");
        ResultSet rs = ps.executeQuery();
        System.out.println("TRACK_GENRE TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds genre to track.
     */
    public static void register(Connection connection, String artist, String album, String track, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.out.println("Track not found");
                return;
            }
            int genreId = TableUtil.getGenreID(connection, genre);
            if (genreId == -1) {
                System.out.println("Genre not found");
                return;
            }
            // make sure row doesn't exist
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_GENRE WHERE trk_id=? and gen_id=?");
            ps.setInt(1, trackId);
            ps.setInt(2, genreId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Genre already associated with track");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO TRACK_GENRE (trk_id, gen_id) VALUES (?, ?)");
            ps.setInt(1, trackId);
            ps.setInt(2, genreId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre not added to track");
                return;
            }
            connection.commit();
            System.out.println("Genre added to track");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a genre from a given track.
     */
    public static void delete(Connection connection, String artist, String album, String track, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.out.println("Track not found");
                return;
            }
            int genreId = TableUtil.getGenreID(connection, genre);
            if (genreId == -1) {
                System.out.println("Genre not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM TRACK_GENRE WHERE trk_id=? AND gen_id=?");
            ps.setInt(1, trackId);
            ps.setInt(2, genreId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre not deleted from track");
            }
            System.out.println("Genre deleted from track");
            connection.commit();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns true if track has that genre.
     */
    public static Boolean contains(Connection connection, String artist, String album, String track, String genre) throws SQLException {
        int trackId = TableUtil.getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            System.out.println("Track not found");
            return false;
        }
        int genreId = TableUtil.getGenreID(connection, genre);
        if (genreId == -1) {
            System.out.println("Genre not found");
            return false;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_GENRE WHERE trk_id=? AND gen_id=?");
        ps.setInt(1, trackId);
        ps.setInt(2, genreId);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the TRACK_GENRE table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_GENRE");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
