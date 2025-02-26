package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrackGenreTable {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * from TRACK_GENRE");
        ResultSet rs = ps.executeQuery();
        System.out.println("GENRE TABLE:");
        TableUtil.print(rs);
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK_GENRE");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
