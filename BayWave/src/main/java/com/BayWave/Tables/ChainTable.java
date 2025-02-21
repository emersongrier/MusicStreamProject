package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChainTable {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from CHAIN_");
        ResultSet rs = ps.executeQuery();
        System.out.println("CHAIN TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Registers a chain into a playlist while also associating it with
     * the required minimum of two tracks via the CHAIN_TRACK entity.
     * Within the chain, the first track will be ordered before the second by default.
     */
    public static void register(Connection connection, String user, String playlist, String artist1, String album1, String track1,
                                String artist2, String album2, String track2) throws SQLException { // at least two songs must be in chain
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE usr_id=? AND lower(ply_name)=lower(?)");
            ps.setInt(1, userId);
            ps.setString(2, playlist);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Playlist not found");
                return;
            }
            rs.next();
            int plyId = rs.getInt("ply_id");
            // make sure neither of those two tracks appear in a chain within that playlist
            // but first, get artist ids, then album ids, then track ids
            ps = connection.prepareStatement("SELECT * FROM ARTIST WHERE lower(art_name)=lower(?)");
            ps.setString(1, artist1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Artist 1 not found");
                return;
            }
            rs.next();
            int artistId1 = rs.getInt("art_id");
            ps = connection.prepareStatement("SELECT * FROM ARTIST WHERE lower(art_name)=lower(?)");
            ps.setString(1, artist2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Artist 2 not found");
                return;
            }
            rs.next();
            int artistId2 = rs.getInt("art_id");

            ps = connection.prepareStatement("SELECT * FROM ALBUM WHERE art_id=? AND lower(alb_name)=lower(?)");
            ps.setInt(1, artistId1);
            ps.setString(2, album1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Album 1 not found");
                return;
            }
            rs.next();
            int albumId1 = rs.getInt("alb_id");
            ps = connection.prepareStatement("SELECT * FROM ALBUM WHERE art_id=? AND lower(alb_name)=lower(?)");
            ps.setInt(1, artistId2);
            ps.setString(2, album2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Album 2 not found");
                return;
            }
            rs.next();
            int albumId2 = rs.getInt("alb_id");

            ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND lower(trk_name)=lower(?)");
            ps.setInt(1, albumId1);
            ps.setString(2, track1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 1 not found");
                return;
            }
            rs.next();
            int trackId1 = rs.getInt("trk_id");
            ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND lower(trk_name)=lower(?)");
            ps.setInt(1, albumId2);
            ps.setString(2, track2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 2 not found");
                return;
            }
            rs.next();
            int trackId2 = rs.getInt("trk_id");

            // check if track IDs are already found within chains associated with that playlist
            // first, find all chains associated with that playlist
            ps = connection.prepareStatement("SELECT * FROM CHAIN WHERE ply_id=?");
            ps.setInt(1, plyId);
            rs = ps.executeQuery();
            // iterate over all chains associated with that playlist,
            // checking associated song IDs
            while (rs.next()) {
                int chainId = rs.getInt("chn_id");
                ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
                ps.setInt(1, chainId);
                ResultSet rs2 = ps.executeQuery();
                while (rs2.next()) {
                    int currTrk = rs2.getInt("trk_id");
                    if (currTrk == trackId1 || currTrk == trackId2) {
                        System.out.println("At least one track is already associated with a chain in this playlist");
                        return;
                    }
                }
            }

            // now, make sure two tracks actually exist in playlist
            ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trackId1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 1 not found in playlist");
                return;
            }
            ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trackId2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 2 not found in playlist");
                return;
            }

            // chain can now be created, with the two songs associated
            ps = connection.prepareStatement("INSERT INTO CHAIN_ (ply_id) VALUES (?) RETURNING chn_id");
            ps.setInt(1, plyId);
            rs = ps.executeQuery(); // executeQuery is used here instead of executeUpdate due to the RETURNING clause
            if (!rs.isBeforeFirst()) {
                System.out.println("Chain not added");
                return;
            }
            rs.next();
            int newChainId = rs.getInt("chn_id");

            // TODO: Trigger for ordering chain tracks
            ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, newChainId);
            ps.setInt(2, trackId1);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not added to chain");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, newChainId);
            ps.setInt(2, trackId2);
            result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not added to chain");
                return;
            }

            connection.commit();
            System.out.println("Chain added, with two associated tracks");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static ArrayList<String[]> getTableForPlaylist(Connection connection, String user, String playlist) throws SQLException {
        int userId = TableUtil.getUserID(connection, user);
        if (userId == -1) {
            System.out.println("User not found");
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE usr_id=? AND lower(ply_name)=?");
        ps.setInt(1, userId);
        ps.setString(2, playlist);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Playlist not found");
            return null;
        }
        rs.next();
        int plyId = rs.getInt("ply_id");
        ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
        ps.setInt(1, plyId);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Chain not found");
            return null;
        }
        return TableUtil.getTable(rs);
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
