/**
 * Helper functions for interacting with the ALBUM_GENRE table, which is an associative entity
 * linking genres to albums.
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

public class AlbumGenreTable {
    /**
     * Prints the ALBUM_GENRE table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from ALBUM_GENRE");
        ResultSet rs = ps.executeQuery();
        System.out.println("ALBUM_GENRE TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds genre to track.
     */
    public static void register(Connection connection, String artist, String album, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            int albumId = TableUtil.getAlbumID(connection, artist, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            int genreId = TableUtil.getGenreID(connection, genre);
            if (genreId == -1) {
                System.out.println("Genre not found");
                return;
            }
            // make sure row doesn't exist
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM_GENRE WHERE alb_id=? and gen_id=?");
            ps.setInt(1, albumId);
            ps.setInt(2, genreId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Genre already associated with album");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO ALBUM_GENRE (alb_id, gen_id) VALUES (?, ?)");
            ps.setInt(1, albumId);
            ps.setInt(2, genreId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre not added to album");
                return;
            }
            connection.commit();
            System.out.println("Genre added to album");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a genre from an album.
     */
    public static void delete(Connection connection, String artist, String album, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            int albumId = TableUtil.getAlbumID(connection, artist, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            int genreId = TableUtil.getGenreID(connection, genre);
            if (genreId == -1) {
                System.out.println("Genre not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM ALBUM_GENRE WHERE alb_id=? AND gen_id=?");
            ps.setInt(1, albumId);
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
     * Returns an ArrayList of String tables. Each string table represents a row in the ALBUM_GENRE table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM_GENRE");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
