/**
 * Helper functions for interacting with the CHAIN table. Each chain is associated with one playlist,
 * and at least two tracks via the CHAIN_TRACK associative entity.
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

public class ChainTable {
    /**
     * Prints the CHAIN table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from CHAIN_");
        ResultSet rs = ps.executeQuery();
        System.out.println("CHAIN TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Registers a chain into a playlist while also associating it with
     * the required minimum of two tracks via the CHAIN_TRACK entity.
     * Within the chain, the first track will be ordered before the second by default,
     * and the same ordering will be enforced within the playlist.
     */
    public static void register(Connection connection, String user, String playlist, String artist1, String album1, String track1,
                                String artist2, String album2, String track2) throws SQLException { // at least two tracks must be in chain
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
            ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
            ps.setInt(1, plyId);
            rs = ps.executeQuery();
            // iterate over all chains associated with that playlist,
            // checking associated track IDs
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

            // chain can now be created, with the two tracks associated
            ps = connection.prepareStatement("INSERT INTO CHAIN_ (ply_id) VALUES (?)", java.sql.PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, plyId);
            int result = ps.executeUpdate(); // executeQuery is used here instead of executeUpdate due to the RETURNING clause
            if (result == 0) {
                System.out.println("Chain not added");
                return;
            }
            // get id of newly inserted chain
            rs = ps.getGeneratedKeys();
            if (!rs.isBeforeFirst()) {
                System.out.println("Could not get chain ID");
                return;
            }
            rs.next();
            int newChainId = rs.getInt(1);

            ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, newChainId);
            ps.setInt(2, trackId1);
            result = ps.executeUpdate();
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
            // change playlist positions
            ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trackId1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 1 not found in playlist");
                return;
            }
            rs.next();
            int plyPos1 = rs.getInt("ply_trk_pos");
            ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trackId2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 2 not found in playlist");
                return;
            }
            rs.next();
            int plyPos2 = rs.getInt("ply_trk_pos");

            if (plyPos1 < plyPos2) { // insert track 2 at plyPos1 + 1
                PlaylistTrackTable.insertAtPosition(connection, user, playlist, artist2, album2, track2, plyPos1 + 1);
            }
            else { // insert track 2 at plyPos1
                PlaylistTrackTable.insertAtPosition(connection, user, playlist, artist2, album2, track2, plyPos1);
            }
            // this results in back to back commits, which is no issue

            connection.commit();
            System.out.println("Chain added, with two associated tracks");
            /*

            When a chain is added, the positions in the playlist must change such that they
            are together, and in order, if they are not already.
            If the first track comes before the second in the playlist, then the second
            track should be inserted at the position of the first track + 1. This position
            is guaranteed to exist because the second track comes after the first.
            However, if the second track comes before the first track,
            the second track should be inserted at the position of the first track, which
            will push it up, making the second track come after.

            When a track is added to a chain, it should follow the same rules as the second
            track when its position was being changed in the playlist. If the track comes
            after the chain, it should be inserted at the position of the last track
            in the chain + 1. But if the track comes before the chain, it should be
            inserted at the position of the last track in the chain.

            If a track is removed from a chain, it should be re-positioned to come after
            the chain. The current position of the track is guaranteed to come before
            that position, so the track should be inserted at the position of the last
            track in the chain. If a chain is deleted, nothing needs to change.

            When a chain is no longer associated with any CHAIN_TRACKs, the chain itself
            should be deleted. If the amount of associated CHAIN_TRACKs goes below two,
            the final CHAIN_TRACK should be deleted, resulting in the CHAIN_TRACK also
            being deleted.

            TODO:
            If position within chain is changed, its position within the playlist should also be changed.
            If position within playlist in changed, and the track is within a chain, it should also
            change the position within the chain. If the new position is outside the chain, it
            shouldn't work.
            */
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes a chain, along with any associated CHAIN_TRACK entities.
     */
    public static void delete(Connection connection, int chainId) throws SQLException {
        try {
            Reset.lock.lock();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM CHAIN_ WHERE chn_id=?");
            ps.setInt(1, chainId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Chain not deleted");
                return;
            }
            // If a chain is deleted, nothing needs to change regarding playlist position.
            connection.commit();
            System.out.println("Chain deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Gets the ID of a chain, given a specified playlist and
     * track currently contained by the chain within that playlist.
     */
    public static int getChainIdWithPlaylistAndTrack(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        // get playlist ID
        int playlistId = TableUtil.getPlaylistID(connection, user, playlist);
        // get track ID
        int trackId = TableUtil.getTrackID(connection, artist, album, track);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("No chains found");
            return -1;
        }
        // check every chain until trk_id is found
        while (rs.next()) {
            int chainId = rs.getInt("chn_id");
            ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? AND trk_id=?");
            ps.setInt(1, chainId);
            ps.setInt(2, trackId);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Chain found");
                return chainId;
            }
        }
        System.out.println("Chain not found");
        return -1;
    }

    /**
     * Returns an ArrayList of String[] where each string is a row of the ResultSet for each chain associated with the given playlist.
     */
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

    /**
     * Returns an ArrayList of strings, each representing a row in the TRACK table associated with the
     * specified chain, and containing the following attributes in order starting from string index 0:
     * trk_id, trk_name, trk_file, trk_pos, trk_lyrics, trk_len, trk_strms, trk_likes, alb_id.
     * The first element of the ArrayList (index 0), is a header containing these attribute names.
     */
    public static ArrayList<String[]> getTracksForChain(Connection connection, int chainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
        ps.setInt(1, chainId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Tracks not found");
            return null;
        }
        return TableUtil.getTable(rs);
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the CHAIN table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
