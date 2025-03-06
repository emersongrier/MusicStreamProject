/**
 * Helper functions for interacting with the COLLABORATE_PLAYLIST table, which is an associative entity
 * linking users to playlists, as collaborators.
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

public class CollaboratePlaylistTable {
    /**
     * Prints the COLLABORATE_PLAYLIST table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_PLAYLIST");
        ResultSet rs = ps.executeQuery();
        System.out.println("COLLABORATE_PLAYLIST TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a collaborator to a playlist.
     */
    public static void register(Connection connection, String collab, String owner, String playlist) throws SQLException {
        try {
            Reset.lock.lock();
            if (collab.equalsIgnoreCase(owner)) {
                System.out.println("Can't set owner of playlist as collaborator");
                return;
            }
            int collabId = TableUtil.getUserID(connection, collab);
            if (collabId == -1) {
                System.out.println("Collaborator not found");
                return;
            }
            int ownerId = TableUtil.getUserID(connection, owner);
            if (ownerId == -1) {
                System.out.println("Owner not found");
                return;
            }
            int playlistId = TableUtil.getPlaylistID(connection, owner, playlist);
            if (playlistId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_PLAYLIST WHERE usr_id=? AND ply_id=?");
            ps.setInt(1, collabId);
            ps.setInt(2, playlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("User already collaborator of playlist");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO COLLABORATE_PLAYLIST (usr_id, ply_id) VALUES (?, ?)");
            ps.setInt(1, collabId);
            ps.setInt(2, playlistId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Collaborator not added");
                return;
            }
            connection.commit();
            System.out.println("Collaborator added");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a collaborator from a playlist.
     */
    public static void delete(Connection connection, String collab, String owner, String playlist) throws SQLException {
        try {
            Reset.lock.lock();
            int collabId = TableUtil.getUserID(connection, collab);
            if (collabId == -1) {
                System.out.println("Collaborator not found");
                return;
            }
            int ownerId = TableUtil.getUserID(connection, owner);
            if (ownerId == -1) {
                System.out.println("Owner not found");
                return;
            }
            int playlistId = TableUtil.getPlaylistID(connection, owner, playlist);
            if (playlistId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM COLLABORATE_PLAYLIST WHERE usr_id=? AND ply_id=?");
            ps.setInt(1, collabId);
            ps.setInt(2, playlistId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Collaborator not deleted");
                return;
            }
            connection.commit();
            System.out.println("Collaborator deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns true if the playlist has that user as a collaborator.
     */
    public static Boolean contains(Connection connection, String user, String playlist, String collab) throws SQLException {
        int playlistId = TableUtil.getPlaylistID(connection, user, playlist);
        if (playlistId == -1) {
            System.out.println("Playlist not found");
            return false;
        }
        int collabId = TableUtil.getUserID(connection, collab);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_PLAYLIST WHERE ply_id=? AND usr_id=?");
        ps.setInt(1, playlistId);
        ps.setInt(2, collabId);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the COLLABORATE_PLAYLIST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_PLAYLIST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
