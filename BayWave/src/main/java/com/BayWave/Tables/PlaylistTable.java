/**
 * Helper functions for interacting with the PLAYLIST table. Each playlist is associated with one
 * primary user (owner), and potentially multiple other users (collaborators). It can also be
 * associated with many songs via the PLAYLIST_TRACK associative entity.
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

public class PlaylistTable {
    /**
     * Prints the PLAYLIST table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from PLAYLIST");
        ResultSet rs = ps.executeQuery();
        System.out.println("PLAYLIST TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Creates a playlist, owned by the specified user.
     */
    public static void register(Connection connection, String user, String name) throws SQLException {
        try {
            Reset.lock.lock();
            // ensure playlist is valid
            if (name.isEmpty()) {
                System.out.println("Playlist name cannot be empty");
                return;
            }
            // get userid
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // playlists need unique names among a particular user's playlists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE USR_ID=? AND LOWER(PLY_NAME)=LOWER(?)");
            ps.setInt(1, userId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("User already has a playlist under that name");
                return;
            }
            ps = connection.prepareStatement(
                    "INSERT INTO PLAYLIST (ply_name, usr_id) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.executeUpdate();
            connection.commit();
            System.out.println("Playlist registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes a playlist, along with any associated PLAYLIST_TRACK entities.
     */
    public static void delete(Connection connection, String user, String name) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // playlists need unique names among a particular user's playlists
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PLAYLIST WHERE USR_ID=? AND LOWER(PLY_NAME)=LOWER(?)");
            ps.setInt(1, userId);
            ps.setString(2, name);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Playlist not deleted");
                return;
            }
            connection.commit();
            System.out.println("Playlist deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Updates the description of a playlist.
     */
    public static void updateDesc(Connection connection, String user, String plyName, String desc) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PLAYLIST SET ply_desc=? WHERE usr_id=? AND LOWER(ply_name)=LOWER(?)");
            ps.setString(1, desc);
            ps.setInt(2, userId);
            ps.setString(3, plyName);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Description not updated");
                return;
            }
            connection.commit();
            System.out.println("Description updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void printDesc(Connection connection, String user, String plyName) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT ply_desc FROM PLAYLIST WHERE usr_id=? AND LOWER(ply_name)=LOWER(?)");
            ps.setInt(1, userId);
            ps.setString(2, plyName);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Playlist not found");
                return;
            }
            rs.next();
            System.out.println(user + "'s " + plyName + " Description: ");
            System.out.println(rs.getString(1));
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateName(Connection connection, String user, String plyName, String newName) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // check if playlist name already exists for that user
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PLAYLIST WHERE LOWER(ply_name)=LOWER(?) AND usr_id=?");
            ps.setString(1, newName);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Playlist name already exists for user, not updated");
                return;
            }
            // playlist name available
            ps = connection.prepareStatement(
                    "UPDATE PLAYLIST SET ply_name=? WHERE LOWER(ply_name)=LOWER(?) AND usr_id=?");
            ps.setString(1, newName);
            ps.setString(2, plyName);
            ps.setInt(3, userId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Playlist name not updated");
                return;
            }
            connection.commit();
            System.out.println("Playlist name updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateCover(Connection connection, String user, String plyName, String cover) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // repeated covers are allowed
            // TODO: Make sure cover link is valid image
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE PLAYLIST SET ply_cvr=? WHERE LOWER(ply_name)=LOWER(?) AND usr_id=?");
            ps.setString(1, cover);
            ps.setString(2, plyName);
            ps.setInt(3, userId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Playlist cover not updated");
                return;
            }
            connection.commit();
            System.out.println("Playlist cover updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void togglePrivate(Connection connection, String user, String playlist) throws SQLException {
        try {
            Reset.lock.lock();
            int playlistId = TableUtil.getPlaylistID(connection, user, playlist);
            if (playlistId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            boolean isPrivate;
            PreparedStatement ps = connection.prepareStatement("SELECT ply_priv FROM PLAYLIST WHERE ply_id=?");
            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Playlist privacy not found");
            }
            rs.next();
            isPrivate = rs.getBoolean("ply_priv");
            ps = connection.prepareStatement("UPDATE PLAYLIST SET ply_priv=? WHERE ply_id=?");
            ps.setBoolean(1, !isPrivate);
            ps.setInt(2, playlistId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Playlist privacy toggle failed");
                return;
            }
            connection.commit();
            System.out.println("Playlist privacy toggled");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the PLAYLIST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
