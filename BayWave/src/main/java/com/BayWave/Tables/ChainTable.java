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
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_");
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
    public static void register(Connection connection, int playlistId, int trackId1, int trackId2) throws SQLException {
        try {
            Reset.lock.lock();
            // check if track IDs are already found within chains associated with that playlist
            // first, find all chains associated with that playlist
            PreparedStatement ps = connection.prepareStatement("SELECT chn_id FROM CHAIN_ WHERE ply_id=?");
            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();
            // iterate over all chains associated with that playlist,
            // checking associated track IDs
            while (rs.next()) {
                int chainId = rs.getInt("chn_id");
                ps = connection.prepareStatement("SELECT trk_id FROM CHAIN_TRACK WHERE chn_id=?");
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
            ps = connection.prepareStatement("SELECT ply_id FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 1 not found in playlist");
                return;
            }
            ps = connection.prepareStatement("SELECT ply_id FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 2 not found in playlist");
                return;
            }

            // chain can now be created, with the two tracks associated
            ps = connection.prepareStatement("INSERT INTO CHAIN_ (ply_id) VALUES (?)", java.sql.PreparedStatement.RETURN_GENERATED_KEYS);
            // the RETURN_GENERATED_KEYS statement allows us to obtain the new chain's id
            ps.setInt(1, playlistId);
            int result = ps.executeUpdate();
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
            // add both tracks to the chain. methods return true if operation fails
            if (addTrackToChain(connection, trackId1, newChainId)) {
                return;
            }
            if (addTrackToChain(connection, trackId2, newChainId)) {
                return;
            }
            // change playlist positions
            ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId1);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 1 not found in playlist");
                return;
            }
            rs.next();
            int plyPos1 = rs.getInt("ply_trk_pos");
            ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, playlistId);
            ps.setInt(2, trackId2);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track 2 not found in playlist");
                return;
            }
            rs.next();
            int plyPos2 = rs.getInt("ply_trk_pos");

            /*
            The way we update the playlist track positions depends on which one comes first,
            due to the nature of the insertAtPosition function. Because inserting a track
            at a new position always moves it past any elements on the way to that position,
            we have different results depending on whether the track moves up or down the track list.
            If track one comes BEFORE track two, and we try to move track two into track one's
            exact position, track two will move past track one relative to its position,
            reversing the order we want. As such, we want to move track two into track one's position
            PLUS ONE. On the other hand, if track one comes AFTER track two, moving track two to track
            one's position still causes track two to move past track one, but in this case
            it is DESIRED BEHAVIOR, because this will result in track two coming after track one.
            */
            if (plyPos1 < plyPos2) { // insert track 2 at plyPos1 + 1
                PlaylistTrackTable.insertAtPosition(connection, playlistId, trackId2, plyPos1 + 1);
            }
            else { // insert track 2 at plyPos1
                PlaylistTrackTable.insertAtPosition(connection, playlistId, trackId2, plyPos1);
            }
            // this results in back to back commits, which is no issue

            connection.commit();
            System.out.println("Chain added, with two associated tracks");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Allows registration using names rather than IDs. Particularly useful for testing purposes.
     */
    public static void register(Connection connection, String user, String playlist, String artist1, String album1, String track1,
                                String artist2, String album2, String track2) throws SQLException { // at least two tracks must be in chain
            int plyId = TableUtil.getPlaylistID(connection, user, playlist);
            if (plyId == -1) {
                System.err.println("Playlist not found");
                return;
            }
            // we must make sure neither of those two tracks appear in a chain within that playlist
            // but first, get track IDs

            int trackId1 = TableUtil.getTrackID(connection, artist1, album1, track1);
            if (trackId1 == -1) {
                System.err.println("Track 1 not found");
                return;
            }
            int trackId2 = TableUtil.getTrackID(connection, artist2, album2, track2);
            if (trackId2 == -1) {
                System.err.println("Track 1 not found");
                return;
            }
            register(connection, plyId, trackId1, trackId2);
    }

    /**
     * Helper function that adds a track to a chain.
     */
    private static boolean addTrackToChain(Connection connection, int trackId1, int newChainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) VALUES (?, ?)");
        ps.setInt(1, newChainId);
        ps.setInt(2, trackId1);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("Track not added to chain");
            return true;
        }
        return false;
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
    public static int getChainIdWithPlaylistAndTrack(Connection connection, int playlistId, int trackId) throws SQLException {
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
     * Gets the ID of a chain using names instead of IDs.
     */
    public static int getChainIdWithPlaylistAndTrack(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        // get playlist ID
        int playlistId = TableUtil.getPlaylistID(connection, user, playlist);
        if (playlistId == -1) {
            System.err.println("Playlist not found");
            return -1;
        }
        // get track ID
        int trackId = TableUtil.getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            System.err.println("Track not found");
            return -1;
        }
        return getChainIdWithPlaylistAndTrack(connection, playlistId, trackId);
    }

    public static ArrayList<String[]> getTableForPlaylist(Connection connection, int playlistId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Chain not found");
            return null;
        }
        return TableUtil.getTable(rs);
    }

    /**
     * Gets table using names instead of an ID.
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
        return getTableForPlaylist(connection, plyId);
    }

    // TODO: Return all chain IDs for a given playlist

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
