package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;
import com.BayWave.Reset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MediaTable {
    /**
     * Prints the MEDIA table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from MEDIA");
        ResultSet rs = ps.executeQuery();
        System.out.println("MEDIA TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, int postId, String mediaFile) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO MEDIA (pst_id, med_file) VALUES (?, ?)");
            ps.setInt(1, postId);
            ps.setString(2, mediaFile);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Media not added to post");
                return;
            }
            connection.commit();
            System.out.println("Media added to post");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, int mediaId) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM MEDIA WHERE med_id=?");
            ps.setInt(1, mediaId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Media not deleted from post");
                return;
            }
            connection.commit();
            System.out.println("Media deleted from post");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the MEDIA table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEDIA");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
