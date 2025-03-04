/**
 * Helper functions for interacting with the ALBUM table. Each album is associated with an artist,
 * and potentially multiple collaborating artists.
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

public class AlbumTable {
    /**
     * Prints the ALBUM table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from ALBUM");
        ResultSet rs = ps.executeQuery();
        System.out.println("ALBUM TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds album to artist.
     */
    public static void register(Connection connection, String artist, String album, String type) throws SQLException {
        try {
            Reset.lock.lock();
            // ensure album name is valid
            if (album.isEmpty()) {
                System.out.println("Album name cannot be empty");
                return;
            }
            // ensure type is valid
            if (!TableUtil.isValidType(type)) {
                System.out.println("Type name is invalid (case-sensitive)");
                return;
            }
            // get artist id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            // check if album exists
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM ALBUM WHERE LOWER(alb_name)=LOWER(?) AND art_id=?"); // case-insensitive
            ps.setString(1, album);
            ps.setInt(2, artId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) { // ResultSet is not empty, name unavailable
                System.out.println("Album name already exists for artist, not registered");
                return;
            }

            ps = connection.prepareStatement(
                    "INSERT INTO ALBUM (alb_type, alb_name, art_id) VALUES (?, ?, ?)");
            ps.setString(1, type);
            ps.setString(2, album);
            ps.setInt(3, artId);
            ps.executeUpdate();
            connection.commit();
            System.out.println("Album registered");
        } finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes an artist's name.
     */
    public static void updateName(Connection connection, String artist, String album, String newName) throws SQLException {
        try {
            Reset.lock.lock();
            // get artist id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }

            // check if new username already exists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
            ps.setString(1, newName);
            ps.setInt(2, artId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Album name already exists for artist, not updated");
                return;
            }
            // username available
            ps = connection.prepareStatement(
                    "UPDATE ALBUM SET alb_name=? WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
            ps.setString(1, newName);
            ps.setString(2, album);
            ps.setInt(3, artId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Album name not updated");
                return;
            }
            connection.commit();
            System.out.println("Album name updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes an album's type to one of three options ("Single", "EP", "LP"), case-sensitive.
     */
    public static void updateType(Connection connection, String artist, String album, String newType) throws SQLException {
        try {
            Reset.lock.lock();
            // get artist id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            // ensure type is valid
            if (!TableUtil.isValidType(newType)) {
                System.out.println("Type name is invalid (case-sensitive)");
                return;
            }

            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE ALBUM SET alb_type=? WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
            ps.setString(1, newType);
            ps.setString(2, album);
            ps.setInt(3, artId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Album type not updated");
                return;
            }
            connection.commit();
            System.out.println("Album type updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the filepath to the album cover image.
     */
    public static void updateCover(Connection connection, String artist, String album, String cover) throws SQLException {
        try {
            Reset.lock.lock();
            // get artist id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            // TODO: CHECK FOR VALID IMAGE LINK
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE ALBUM SET alb_cvr=? WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
            ps.setString(1, cover);
            ps.setString(2, album);
            ps.setInt(3, artId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Album cover not updated");
                return;
            }
            connection.commit();
            System.out.println("Album cover updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes an album, along with any associated tracks.
     */
    public static void delete(Connection connection, String artist, String album) throws SQLException {
        try {
            Reset.lock.lock();
            // get artist id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM ALBUM WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
            ps.setString(1, album);
            ps.setInt(2, artId);

            int results = ps.executeUpdate();
            if (results == 0) {
                System.out.println("Album not deleted");
                return;
            }
            connection.commit();
            System.out.println("Album deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Toggles the boolean value indicating whether the album is private (true = private, false = public).
     */
    public static void togglePrivate(Connection connection, String artist, String album) throws SQLException {
        try {
            Reset.lock.lock();
            int albumId = TableUtil.getAlbumID(connection, artist, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            boolean isPrivate;
            PreparedStatement ps = connection.prepareStatement("SELECT alb_priv FROM ALBUM WHERE alb_id=?");
            ps.setInt(1, albumId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Album privacy not found");
            }
            rs.next();
            isPrivate = rs.getBoolean("alb_priv");
            ps = connection.prepareStatement("UPDATE ALBUM SET alb_priv=? WHERE alb_id=?");
            ps.setBoolean(1, !isPrivate);
            ps.setInt(2, albumId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Album privacy toggle failed");
                return;
            }
            connection.commit();
            System.out.println("Album privacy toggled");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the ALBUM table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }

    /**
     * Returns a string representing the specified row in the ALBUM table,
     * which contains the following attributes in order starting from index 0:
     * alb_id, alb_type, alb_name, alb_cvr, alb_trks, alb_likes, alb_priv, art_id.
     */
    public static String[] getAlbum(Connection connection, String artist, String album) throws SQLException {
        int albumId = TableUtil.getAlbumID(connection, artist, album);
        if (albumId == -1) {
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM WHERE alb_id=?");
        ps.setInt(1, albumId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Album not found");
            return null;
        }
        return TableUtil.getFirstStringTable(rs);
    }

    /**
     * Returns an ArrayList of strings, each representing a row in the TRACK table associated with the
     * specified album, and containing the following attributes in order starting from string index 0:
     * trk_id, trk_name, trk_file, trk_pos, trk_lyrics, trk_len, trk_strms, trk_likes, alb_id.
     * The first element of the ArrayList (index 0), is a header containing these attribute names.
     */
    public static ArrayList<String[]> getTracks(Connection connection, String artist, String album) throws SQLException {
        int albId = TableUtil.getAlbumID(connection, artist, album);
        if (albId == -1) {
            System.out.println("Album not found");
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=?");
        ps.setInt(1, albId);
        ResultSet rs = ps.executeQuery();
        return TableUtil.getTable(rs);
    }
}
