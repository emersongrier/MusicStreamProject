/**
 * Helper functions for interacting with the COLLABORATE_ALBUM table, which is an associative entity
 * linking artists to albums, as collaborators.
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

public class CollaborateAlbumTable {
    /**
     * Prints the COLLABORATE_ALBUM table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from COLLABORATE_ALBUM");
        ResultSet rs = ps.executeQuery();
        System.out.println("COLLABORATE_ALBUM TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a collaborator to an album.
     */
    public static void register(Connection connection, String collab, String primary, String album) throws SQLException {
        try {
            Reset.lock.lock();
            if (collab.equalsIgnoreCase(primary)) {
                System.out.println("Can't set primary artist as collaborator");
                return;
            }
            int collabId = TableUtil.getArtistID(connection, collab);
            if (collabId == -1) {
                System.out.println("Collaborator not found");
                return;
            }
            int primaryId = TableUtil.getArtistID(connection, primary);
            if (primaryId == -1) {
                System.out.println("Primary artist not found");
                return;
            }
            int albumId = TableUtil.getAlbumID(connection, primary, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_ALBUM WHERE art_id=? AND alb_id=?");
            ps.setInt(1, collabId);
            ps.setInt(2, albumId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Collaborator already exists");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO COLLABORATE_ALBUM(art_id, alb_id) VALUES(?, ?)");
            ps.setInt(1, collabId);
            ps.setInt(2, albumId);
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
     * Removes a collaborator from an album.
     */
    public static void delete(Connection connection, String collab, String primary, String album) throws SQLException {
        try {
            Reset.lock.lock();
            int collabId = TableUtil.getArtistID(connection, collab);
            if (collabId == -1) {
                System.out.println("Collaborator not found");
                return;
            }
            int primaryId = TableUtil.getArtistID(connection, primary);
            if (primaryId == -1) {
                System.out.println("Primary artist not found");
                return;
            }
            int albumId = TableUtil.getAlbumID(connection, primary, album);
            if (albumId == -1) {
                System.out.println("Album not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM COLLABORATE_ALBUM WHERE art_id=? AND alb_id=?");
            ps.setInt(1, collabId);
            ps.setInt(2, albumId);
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
     * Returns an ArrayList of String tables. Each string table represents a row in the COLLABORATE_ALBUM table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM COLLABORATE_ALBUM");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
