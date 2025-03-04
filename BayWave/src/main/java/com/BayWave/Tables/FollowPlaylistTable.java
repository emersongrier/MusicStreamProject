/**
 * Helper functions for interacting with the FOLLOW_PLAYLIST table, which is an associative entity
 * linking users to playlists, as followers.
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

public class FollowPlaylistTable {
    /**
     * Prints the FOLLOW_PLAYLIST table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from FOLLOW_PLAYLIST");
        ResultSet rs = ps.executeQuery();
        System.out.println("FOLLOW_PLAYLIST TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds user to playlist's followers.
     */
    public static void register(Connection connection, String owner, String playlist, String follower) throws SQLException {
        try {
            Reset.lock.lock();
            if (follower.equalsIgnoreCase(owner)) {
                System.out.println("User can't follow their own playlist");
                return;
            }
            Reset.lock.lock();
            // obtain ply_id and usr_id of follower
            int plyId = TableUtil.getPlaylistID(connection, owner, playlist);
            if (plyId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            int followerId = TableUtil.getUserID(connection, follower);
            if (followerId == -1) {
                System.out.println("User not found");
                return;
            }

            // check if FOLLOW_PLAYLIST already exists

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FOLLOW_PLAYLIST WHERE usr_id=? AND ply_id=?");
            ps.setInt(1, followerId);
            ps.setInt(2, plyId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("FOLLOW_PLAYLIST already exists");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO FOLLOW_PLAYLIST (usr_id, ply_id) VALUES (?, ?)");
            ps.setInt(1, followerId);
            ps.setInt(2, plyId);
            ps.executeUpdate();
            connection.commit();
            System.out.println("FOLLOW_PLAYLIST registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes user as a follower of the specified playlist.
     */
    public static void delete(Connection connection, String owner, String playlist, String follower) throws SQLException {
        try {
            Reset.lock.lock();
            // obtain ply_id and usr_id of follower
            int plyId = TableUtil.getPlaylistID(connection, owner, playlist);
            if (plyId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            int followerId = TableUtil.getUserID(connection, follower);
            if (followerId == -1) {
                System.out.println("User not found");
                return;
            }

            PreparedStatement ps = connection.prepareStatement("DELETE FROM FOLLOW_PLAYLIST WHERE usr_id=? AND ply_id=?");
            ps.setInt(1, followerId);
            ps.setInt(2, plyId);
            int count = ps.executeUpdate();
            if (count == 0) {
                System.out.println("FOLLOW_PLAYLIST not deleted");
            }
            connection.commit();
            System.out.println("FOLLOW_PLAYLIST deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the FOLLOW_PLAYLIST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FOLLOW_PLAYLIST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
