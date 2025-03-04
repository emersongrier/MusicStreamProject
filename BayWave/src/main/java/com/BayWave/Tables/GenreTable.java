/**
 * Helper functions for interacting with the GENRE table.
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

public class GenreTable {
    /**
     * Prints the GENRE table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from GENRE");
        ResultSet rs = ps.executeQuery();
        System.out.println("GENRE TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a genre to the GENRE table.
     */
    public static void register(Connection connection, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            // check if genre already exists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM GENRE WHERE LOWER(gen_name)=LOWER(?)");
            ps.setString(1, genre);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Genre already exists");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO GENRE (gen_name) VALUES (?)");
            ps.setString(1, genre);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre not registered");
                return;
            }
            connection.commit();
            System.out.println("Genre registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes a genre from the GENRE table.
     */
    public static void delete(Connection connection, String genre) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM GENRE WHERE LOWER(gen_name)=LOWER(?)");
            ps.setString(1, genre);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre not deleted");
                return;
            }
            connection.commit();
            System.out.println("Genre deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Changes the name of a genre.
     */
    public static void updateName(Connection connection, String currGenre, String newGenre) throws SQLException {
        try {
            Reset.lock.lock();
            // make sure new genre name doesn't already exist
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM GENRE WHERE LOWER(gen_name)=LOWER(?)");
            ps.setString(1, "newGenre");
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Genre already exists");
                return;
            }
            ps = connection.prepareStatement("UPDATE GENRE SET GEN_NAME=? WHERE LOWER(GEN_NAME)=LOWER(?)");
            ps.setString(1, newGenre);
            ps.setString(2, currGenre);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Genre name not updated");
                return;
            }
            connection.commit();
            System.out.println("Genre name updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the GENRE table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM GENRE");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
