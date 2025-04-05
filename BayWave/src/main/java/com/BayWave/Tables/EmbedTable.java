package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmbedTable {
    /**
     * Prints the EMBED table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM EMBED");
        ResultSet rs = ps.executeQuery();
        System.out.println("MEDIA TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Creates an entry in the embed table, associated with the given post, assuming there isn't already
     * such an entry in the table.
     */
    public static void register(Connection connection, int postId, String embedType, int embedId) throws SQLException {
        if (!TableUtil.isValidEmbed(embedType)) {
            System.err.println("Invalid embed type. Options are: Artist, Album, Track, Playlist (case-sensitive)");
            return;
        }

        if (!TableUtil.embedExists(connection, embedType, embedId)) {
            System.out.println("Embedded content not found");
            return;
        }

        // make sure post doesn't already have embedded content
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM EMBED WHERE pst_id=?");
        ps.setInt(1, postId);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("Post already has embedded content, delete it first.");
            return;
        }

        ps = connection.prepareStatement("INSERT INTO EMBED (pst_id, emb_id, emb_type) VALUES (?, ?, ?)");
        ps.setInt(1, postId);
        ps.setInt(2, embedId);
        ps.setString(3, embedType);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.err.println("Content not embedded to post.");
            return;
        }
        connection.commit();
        System.out.println("Content embedded to post.");
    }

    /**
     * Returns a string representing the specified row in the EMBED table,
     * which contains the following attributes in order starting from index 0:
     * pst_id, emb_id, emb_type.
     */
    public static String[] getEmbedForPost(Connection connection, int postId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM EMBED WHERE pst_id=?");
        ps.setInt(1, postId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Embedded content not found");
            return null;
        }
        return TableUtil.getFirstStringTable(rs);
    }

    /**
     * Removes embedded content from a post.
     */
    public static void delete(Connection connection, int postId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM EMBED WHERE pst_id=?");
        ps.setInt(1, postId);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.err.println("Embedded content not deleted from post.");
            return;
        }
        connection.commit();
        System.out.println("Embedded content deleted from post.");
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the EMBED table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM EMBED");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
