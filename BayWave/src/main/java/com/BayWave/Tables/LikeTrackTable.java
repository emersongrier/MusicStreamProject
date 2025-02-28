package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LikeTrackTable {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from LIKE_TRACK");
        ResultSet rs = ps.executeQuery();
        System.out.println("LIKE_TRACK TABLE:");
        TableUtil.print(rs);
    }

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

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
