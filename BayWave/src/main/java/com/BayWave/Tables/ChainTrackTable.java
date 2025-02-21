package com.BayWave.Tables;

import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChainTrackTable {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from CHAIN_TRACK");
        ResultSet rs = ps.executeQuery();
        System.out.println("CHAIN_TRACK TABLE:");
        TableUtil.print(rs);
    }

    public static ArrayList<String[]> getTableForChain(Connection connection, int chainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE CHN_ID=?");
        ps.setInt(1, chainId);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("Chain not found");
            return null;
        }
        return TableUtil.getTable(rs);
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
