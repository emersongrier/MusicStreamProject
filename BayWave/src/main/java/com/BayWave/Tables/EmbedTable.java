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

    public static void register(Connection connection, int postId, String embedType, int embedId) throws SQLException {
        if (!TableUtil.isValidEmbed(embedType)) {
            System.err.println("Invalid embed type. Options are: Artist, Album, Track, Playlist (case-sensitive)");
            return;
        }

        if (!TableUtil.embedExists(connection, embedType, embedId)) {
            System.out.println("Embedded content not found");
            return;
        }

        PreparedStatement ps = connection.prepareStatement("INSERT INTO EMBED (pst_id, emb_id, emb_type) VALUES (?, ?, ?)");
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
