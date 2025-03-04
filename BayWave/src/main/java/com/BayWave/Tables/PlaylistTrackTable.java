package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaylistTrackTable {
    /**
     * Prints the PLAYLIST_TRACK table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from PLAYLIST_TRACK");
        ResultSet rs = ps.executeQuery();
        System.out.println("PLAYLIST_TRACK TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a track to a playlist.
     */
    public static void register(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            int usrId = TableUtil.getUserID(connection, user);
            if (usrId == -1) {
                System.out.println("User not found");
                return;
            }
            int trkId = TableUtil.getTrackID(connection, artist, album, track);
            if (trkId == -1) {
                System.out.println("Track not found");
                return;
            }
            int plyId = TableUtil.getPlaylistID(connection, user, playlist);
            if (plyId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            // check if song is already in that playlist
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE trk_id=? AND ply_id=?");
            ps.setInt(1, trkId);
            ps.setInt(2, plyId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Track is already in playlist");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO PLAYLIST_TRACK (ply_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, plyId);
            ps.setInt(2, trkId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("PlaylistTrack not registered");
                return;
            }
            connection.commit();
            System.out.println("PlaylistTrack registered"); // TODO: Order track in playlist via trigger
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a track from a playlist.
     */
    public static void delete(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            if (track.isEmpty()) {
                System.out.println("Track name cannot be empty");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // get playlist id
            int plyId = TableUtil.getPlaylistID(connection, user, playlist);
            if (plyId == -1) {
                System.out.println("Playlist not found");
                return;
            }

            int trkId = TableUtil.getTrackID(connection, artist, album, track);
            if (trkId == -1) {
                System.out.println("Track not found");
                return;
            }

            PreparedStatement ps = connection.prepareStatement("DELETE FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trkId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not deleted");
                return;
            }
            System.out.println("Track deleted from playlist");
            connection.commit();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /* TODO: Add support for swapping chains, automatically move songs in chains together, etc
       This should also impact registering a chain. When you swap or insert using a song from a chain, the entire
       chain is automatically moved as if the chain was one song.
     */


    /**
     * Swaps the track with another track within its playlist, at a given position relative to the playlist.
     * If the tracks are in a chain, this will also adjust their positions within the chain accordingly.
     * If one track is in a chain, and the other track is not within that same chain, this will have
     * no effect.
     */
    public static void swapPosition(Connection connection, String user, String playlist, String artist, String album, String track, int newPos) throws SQLException {
        try {
            Reset.lock.lock();

            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            // get playlist id
            int plyId = TableUtil.getPlaylistID(connection, user, playlist);
            if (plyId == -1) {
                System.out.println("Playlist not found");
                return;
            }
            // get track id
            int trkId = TableUtil.getTrackID(connection, artist, album, track);
            if (trkId == -1) {
                System.out.println("Track not found");
                return;
            }
            // check that a track exists with the new position
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? and trk_id=?");
            ps.setInt(1, plyId);
            ps.setInt(2, trkId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkPos1 = rs.getInt("ply_trk_pos");
            int trkId1 = rs.getInt("trk_id");
            ps = connection.prepareStatement(
                    "SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND ply_trk_pos=?");
            ps.setInt(1, plyId);
            ps.setInt(2, newPos);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track position not found");
                return;
            }
            rs.next();
            int trkPos2 = rs.getInt("ply_trk_pos");
            int trkId2 = rs.getInt("trk_id");
            if (trkPos1 == trkPos2) {
                System.out.println("Track already at position specified");
                return;
            }

            int posDifference = trkPos2 - trkPos1;

            // check if track is in chain
            int chainPos = TableUtil.getChainPos(connection, user, playlist, artist, album, track);
            if (chainPos != -1) { // if track is in a chain
                int chainId = TableUtil.getChainID(connection, user, playlist, artist, album, track);
                if (chainId == -1) {
                    System.out.println("Chain ID not found");
                    return;
                }
                System.out.println("chainPos: " + chainPos + ", posDifference: " + posDifference);
                ChainTrackTable.swapPosition(connection, chainId, artist, album, track, chainPos + posDifference);
                connection.commit();
                return;
            }

            ps = connection.prepareStatement("UPDATE PLAYLIST_TRACK SET ply_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos2);
            ps.setInt(2, trkId1);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Track position not updated");
                return;
            }
            ps = connection.prepareStatement("UPDATE PLAYLIST_TRACK SET ply_trk_pos=? WHERE trk_id=?");
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

    /**
     * Inserts the track at a given position of another track within its playlist, moving past it.
     * If the tracks are in a chain, this will also adjust their positions within the chain accordingly.
     * If one track is in a chain, and the other track is not within that same chain, this will have
     * no effect.
     */
    public static void insertAtPosition(Connection connection, String user, String playlist, String artist, String album, String track, int newPos) throws SQLException {
        int userId = TableUtil.getUserID(connection, user);
        if (userId == -1) {
            System.out.println("User not found");
            return;
        }
        // get playlist id
        int plyId = TableUtil.getPlaylistID(connection, user, playlist);
        if (plyId == -1) {
            System.out.println("Playlist not found");
            return;
        }
        int trkId = TableUtil.getTrackID(connection, artist, album, track);
        if (trkId == -1) {
            System.out.println("Track not found");
            return;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? and trk_id=?");
        ps.setInt(1, plyId);
        ps.setInt(2, trkId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track position not found");
        }
        rs.next();
        int currPos = rs.getInt("ply_trk_pos");
        if (currPos == newPos) {
            System.out.println("Track already at position specified");
            return;
        }

        int posDifference = newPos - currPos;

        // check if track is in chain
        int chainPos = TableUtil.getChainPos(connection, user, playlist, artist, album, track);
        if (chainPos != -1) { // if track is in a chain
            int chainId = TableUtil.getChainID(connection, user, playlist, artist, album, track);
            if (chainId == -1) {
                System.out.println("Chain ID not found");
                return;
            }
            ChainTrackTable.insertAtPosition(connection, chainId, artist, album, track, chainPos + posDifference);
            connection.commit();
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
        ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND ply_trk_pos=?");
        ps.setInt(1, plyId);
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
        ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND ply_trk_pos>=? AND ply_trk_pos<=?");
        ps.setInt(1, plyId);
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
            int thisPos = rs.getInt("ply_trk_pos");
            if (thisId != trkId) {
                System.out.println("UPDATING TRACK");
                ps = connection.prepareStatement("UPDATE PLAYLIST_TRACK SET ply_trk_pos=? WHERE ply_id=? AND trk_id=?");
                int updatedPos = thisPos + delta;
                ps.setInt(1, updatedPos);
                ps.setInt(2, plyId);
                ps.setInt(3, thisId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Track position update failed");
                    return;
                }
            }
        }
        // set track's trk_pos to newPos
        ps = connection.prepareStatement("UPDATE PLAYLIST_TRACK SET ply_trk_pos=? WHERE ply_id=? AND trk_id=?");
        ps.setInt(1, newPos);
        ps.setInt(2, plyId);
        ps.setInt(3, trkId);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("Track position not updated");
            return;
        }
        System.out.println("Track positions updated");
        connection.commit();
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the PLAYLIST_TRACK table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
