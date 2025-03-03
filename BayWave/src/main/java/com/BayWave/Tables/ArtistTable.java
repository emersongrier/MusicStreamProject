package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;
import com.BayWave.Reset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArtistTable {
    public static void register(Connection connection, String name) throws SQLException {
        try {
            Reset.lock.lock();
            // ensure username is valid
            if (name.isEmpty()) {
                System.out.println("Artist name cannot be empty");
                return;
            }
            // check if user exists
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM ARTIST WHERE LOWER(art_name)=LOWER(?)"); // case-insensitive
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) { // ResultSet is not empty, name unavailable
                System.out.println("Artist name already exists, user not registered");
                return;
            }


            ps = connection.prepareStatement(
                    "INSERT INTO ARTIST (art_name) VALUES (?)");
            ps.setString(1, name);
            ps.executeUpdate();
            connection.commit();
            System.out.println("Artist registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes an artist, along with any associated albums, and any tracks associated with those albums.
     */
    public static void delete(Connection connection, String name) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, name);
            int results = ps.executeUpdate();
            if (results == 0) {
                System.out.println("Artist not deleted");
                return;
            }
            connection.commit();
            System.out.println("Artist deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateBio(Connection connection, String name, String bio) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE ARTIST SET art_bio=? WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, bio);
            ps.setString(2, name);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Bio not updated");
                return;
            }
            connection.commit();
            System.out.println("Bio updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateName(Connection connection, String name, String newName) throws SQLException {
        try {
            Reset.lock.lock();
            // check if new username already exists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, newName);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Artist name already exists, not updated");
                return;
            }
            // username available
            ps = connection.prepareStatement(
                    "UPDATE ARTIST SET art_name=? WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, newName);
            ps.setString(2, name);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Artist name not updated");
                return;
            }
            connection.commit();
            System.out.println("Artist name updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Prints the ARTIST table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from ARTIST");
        ResultSet rs = ps.executeQuery();
        System.out.println("ARTIST TABLE:");
        TableUtil.print(rs);
    }

    public static void printBio(Connection connection, String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT art_bio FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        System.out.println(name + " Bio: ");
        System.out.println(rs.getString("art_bio"));
    }

    public static String[] getArtist(Connection connection, String artist) throws SQLException {
        int artistId = TableUtil.getArtistID(connection, artist);
        if (artistId == -1) {
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ARTIST WHERE art_id=?");
        ps.setInt(1, artistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Album not found");
            return null;
        }
        return TableUtil.getFirstStringTable(rs);
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the ARTIST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM ARTIST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
