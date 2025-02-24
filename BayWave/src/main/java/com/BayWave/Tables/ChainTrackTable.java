package com.BayWave.Tables;

import com.BayWave.Reset;
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

    /**
     * Adds a new CHAIN_TRACK row, associating a given chain with a given track.
     */
    public static void addTrack(Connection connection, int chainId, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            // check if track is already in a chain in that playlist
            // get playlist id
            int plyId = TableUtil.getPlaylistIdOfChain(connection, chainId);
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
            ps.setInt(1, plyId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No chain found associated with playlist");
                return;
            }
            while (rs.next()) {
                ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
                ps.setInt(1, rs.getInt("chn_id"));
                ResultSet rs2 = ps.executeQuery();
                if (!rs2.isBeforeFirst()) {
                    System.out.println("Chain not found");
                    return;
                }
                while (rs2.next()) {
                    int currTrackId = rs2.getInt("trk_id");
                    if (currTrackId == trackId) {
                        System.out.println("Track already in chain within playlist");
                        return;
                    }
                }
            }

            ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) values (?, ?)");
            ps.setInt(1, chainId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("CHAIN_TRACK not added");
                return;
            }
            connection.commit();
            System.out.println("CHAIN_TRACK added");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static ArrayList<String[]> getTableForChain(Connection connection, int chainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
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
