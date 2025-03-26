/**
 * Helper functions for interacting with the LIKE_POST table, which is an associative entity
 * linking users to post, indicating that the user added the post to their likes.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LikePostTable {
    /**
     * Prints the LIKE_TRACK table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from LIKE_POST");
        ResultSet rs = ps.executeQuery();
        System.out.println("LIKE_POST TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String username, int userId) throws SQLException {

    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the LIKE_POST table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM LIKE_POST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
