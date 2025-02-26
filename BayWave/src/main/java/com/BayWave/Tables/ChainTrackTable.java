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

    /**
     * Removes a specified track from a specified chain provided that the chain has at least
     * three tracks, enforcing that the chain will always have at least two tracks.
     */
    public static void removeTrack(Connection connection, int chainId, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            // make sure chain has at least three songs before removing (chains must have at least two)
            int trackCount = 0;
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
            ps.setInt(1, chainId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                trackCount++;
            }
            if (trackCount < 3) {
                System.out.println("Chain must always have at least two tracks, not deleted");
                return;
            }
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.out.println("Track not found");
                return;
            }
            ps = connection.prepareStatement("DELETE FROM CHAIN_TRACK WHERE chn_id=? AND trk_id=?");
            ps.setInt(1, chainId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not removed");
                return;
            }
            connection.commit();
            System.out.println("Track removed");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void swapPosition(Connection connection, int chainId, String artist, String album, String track, int newPos) throws SQLException {
        try {
            Reset.lock.lock();
            // get track id
            int trkId = TableUtil.getTrackID(connection, artist, album, track);
            if (trkId == -1) {
                System.out.println("Track not found");
                return;
            }
            // check that a track exists with the new position
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CHAIN_TRACK WHERE chn_id=? and trk_id=?");
            ps.setInt(1, chainId);
            ps.setInt(2, trkId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkPos1 = rs.getInt("chn_trk_pos");
            int trkId1 = rs.getInt("trk_id");
            ps = connection.prepareStatement(
                    "SELECT * FROM CHAIN_TRACK WHERE chn_id=? AND chn_trk_pos=?");
            ps.setInt(1, chainId);
            ps.setInt(2, newPos);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track position not found");
                return;
            }
            rs.next();
            int trkPos2 = rs.getInt("chn_trk_pos");
            int trkId2 = rs.getInt("trk_id");
            if (trkPos1 == trkPos2) {
                System.out.println("Track already at position specified");
                return;
            }
            ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos2);
            ps.setInt(2, trkId1);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Track position not updated");
                return;
            }
            ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos1);
            ps.setInt(2, trkId2);
            int updated2 = ps.executeUpdate();
            if (updated2 == 0) {
                System.out.println("Track position not updated");
                return;
            }
            connection.commit();
            System.out.println("Track position updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void insertAtPosition(Connection connection, int chainId, String artist, String album, String track, int newPos) throws SQLException {
        int trkId = TableUtil.getTrackID(connection, artist, album, track);
        if (trkId == -1) {
            System.out.println("Track not found");
            return;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? and trk_id=?");
        ps.setInt(1, chainId);
        ps.setInt(2, trkId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track position not found");
            return;
        }
        rs.next();
        int currPos = rs.getInt("chn_trk_pos");
        if (currPos == newPos) {
            System.out.println("Track already at position specified");
            return;
        }
        int delta;
        if (newPos > currPos) { // determines whether elements are incremented or decremented
            delta = -1;
        }
        else {
            delta = 1;
        }
        // make sure track position exists
        ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? AND chn_trk_pos=?");
        ps.setInt(1, chainId);
        ps.setInt(2, newPos);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track position not found");
            return;
        }
        rs.next();
        // if newPos > currPos, decrement every track position between currPos and newPos (inclusive),
        // except track's trk_pos, which will become newPos
        // if newPos < currPos, increment every track position between currPos and newPos (inclusive)
        ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? AND chn_trk_pos>=? AND chn_trk_pos<=?");
        ps.setInt(1, chainId);
        if (delta == -1) {
            System.out.println("DELTA -1");
            ps.setInt(2, currPos);
            ps.setInt(3, newPos);
        }
        else {
            System.out.println("DELTA 1");
            ps.setInt(2, newPos);
            ps.setInt(3, currPos);
        }
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Error obtaining track positions");
            return;
        }
        while (rs.next()) {
            int thisId = rs.getInt("trk_id");
            int thisPos = rs.getInt("chn_trk_pos");
            if (thisId != trkId) {
                System.out.println("UPDATING TRACK");
                ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE chn_id=? AND trk_id=?");
                int updatedPos = thisPos + delta;
                ps.setInt(1, updatedPos);
                ps.setInt(2, chainId);
                ps.setInt(3, thisId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Track position update failed");
                    return;
                }
            }
        }
        // set track's trk_pos to newPos
        ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE chn_id=? AND trk_id=?");
        ps.setInt(1, newPos);
        ps.setInt(2, chainId);
        ps.setInt(3, trkId);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("Track position not updated");
            return;
        }
        System.out.println("Track positions updated");
        connection.commit();
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