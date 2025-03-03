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
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK");
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
            if (plyId == -1) {
                System.err.println("Playlist ID not found");
                return;
            }
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.err.println("Track ID not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
            ps.setInt(1, plyId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("No chain found associated with playlist");
                return;
            }
            while (rs.next()) {
                ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
                ps.setInt(1, rs.getInt("chn_id"));
                ResultSet rs2 = ps.executeQuery();
                if (!rs2.isBeforeFirst()) {
                    System.err.println("Chain not found");
                    return;
                }
                while (rs2.next()) {
                    int currTrackId = rs2.getInt("trk_id");
                    if (currTrackId == trackId) {
                        System.err.println("Track already in chain within playlist");
                        return;
                    }
                }
            }

            ps = connection.prepareStatement("INSERT INTO CHAIN_TRACK (chn_id, trk_id) values (?, ?)");
            ps.setInt(1, chainId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("CHAIN_TRACK not added");
                return;
            }

            // update track's position in playlist
            // first, obtain highest playlist position within the chain, excluding the one being added
            ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
            ps.setInt(1, chainId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Chain tracks not found");
                return;
            }
            int highestPos = -1;
            int currPos = -1;
            while (rs.next()) {
                int currTrackId = rs.getInt("trk_id");
                ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
                ps.setInt(1, plyId);
                ps.setInt(2, currTrackId);
                ResultSet rs2 = ps.executeQuery();
                if (!rs2.isBeforeFirst()) {
                    System.err.println("Playlist track not found");
                    return;
                }
                rs2.next();
                if (currTrackId != trackId) {
                    int currPlaylistPos = rs2.getInt("ply_trk_pos");
                    if (currPlaylistPos > highestPos) {
                        highestPos = currPlaylistPos;
                    }
                }
                else {
                    currPos = rs2.getInt("ply_trk_pos");
                }
            }
            if (highestPos == -1) {
                System.err.println("Highest track position not found");
                return;
            }
            if (currPos == -1) {
                System.err.println("Current track position not found");
                return;
            }
            // get user and playlist names
            String playlist = TableUtil.getPlaylistNameFromId(connection, plyId);
            if (playlist == null) {
                System.err.println("Playlist name not found");
                return;
            }
            ps = connection.prepareStatement("SELECT usr_id FROM PLAYLIST WHERE ply_id=?");
            ps.setInt(1, plyId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("User ID not found");
                return;
            }
            rs.next();
            String user = TableUtil.getUsernameFromId(connection, rs.getInt("usr_id"));
            if (user == null) {
                System.err.println("Username not found");
                return;
            }

            if (highestPos < currPos) { // insert track 2 at plyPos1 + 1
                PlaylistTrackTable.insertAtPosition(connection, user, playlist, artist, album, track, highestPos + 1);
            }
            else { // insert track 2 at plyPos1
                PlaylistTrackTable.insertAtPosition(connection, user, playlist, artist, album, track, highestPos);
            }

            /*
            When a track is added to a chain, it should follow the same rules as the second
            track when its position was being changed in the playlist. If the track comes
            after the chain, it should be inserted at the position of the last track
            in the chain + 1. But if the track comes before the chain, it should be
            inserted at the position of the last track in the chain.
             */

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
     * Track's position in playlist will be shifted after the chain.
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
                System.err.println("Chain must always have at least two tracks, not deleted");
                return;
            }
            int trackId = TableUtil.getTrackID(connection, artist, album, track);
            if (trackId == -1) {
                System.err.println("Track not found");
                return;
            }
            int plyId = TableUtil.getPlaylistIdOfChain(connection, chainId);
            if (plyId == -1) {
                System.err.println("Playlist ID not found");
                return;
            }

            ps = connection.prepareStatement("DELETE FROM CHAIN_TRACK WHERE chn_id=? AND trk_id=?");
            ps.setInt(1, chainId);
            ps.setInt(2, trackId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Track not removed");
                return;
            }
            // update track's position in playlist

            ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
            ps.setInt(1, chainId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Chain tracks not found");
                return;
            }
            int highestPos = -1;
            while (rs.next()) {
                int currTrackId = rs.getInt("trk_id");
                ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
                ps.setInt(1, plyId);
                ps.setInt(2, currTrackId);
                ResultSet rs2 = ps.executeQuery();
                if (!rs2.isBeforeFirst()) {
                    System.err.println("Playlist track not found");
                    return;
                }
                rs2.next();
                if (currTrackId != trackId) {
                    int currPlaylistPos = rs2.getInt("ply_trk_pos");
                    if (currPlaylistPos > highestPos) {
                        highestPos = currPlaylistPos;
                    }
                }
            }
            if (highestPos == -1) {
                System.err.println("Highest track position not found");
                return;
            }

            // get user and playlist names
            String playlist = TableUtil.getPlaylistNameFromId(connection, plyId);
            if (playlist == null) {
                System.err.println("Playlist name not found");
                return;
            }
            ps = connection.prepareStatement("SELECT usr_id FROM PLAYLIST WHERE ply_id=?");
            ps.setInt(1, plyId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("User ID not found");
                return;
            }
            rs.next();
            String user = TableUtil.getUsernameFromId(connection, rs.getInt("usr_id"));
            if (user == null) {
                System.err.println("Username not found");
                return;
            }

            PlaylistTrackTable.insertAtPosition(connection, user, playlist, artist, album, track, highestPos);

            /*
            If a track is removed from a chain, it should be re-positioned to come after
            the chain. The current position of the track is guaranteed to come before
            that position, so the track should be inserted at the position of the last
            track in the chain.
             */
            connection.commit();
            System.out.println("Track removed");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Dedicated version of PlaylistTrackTable's swap method such that it doesn't call chain's version.
     */
    private static void playlistSwapPosition(Connection connection, String user, String playlist, String artist, String album, String track, int newPos) throws SQLException {
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
            /*
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
            }*/

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
     * Dedicated version of PlaylistTrackTable's insert method such that it doesn't call chain's version.
     */
    private static void playlistInsertAtPosition(Connection connection, String user, String playlist, String artist, String album, String track, int newPos) throws SQLException {
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
        /*
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
        */
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

    // any of these position changes should be mirrored in the playlist
    // TODO: I may have to make dedicated versions of the playlist functions for use by these ones

    public static void swapPosition(Connection connection, int chainId, String artist, String album, String track, int newPos) throws SQLException {
        try {
            Reset.lock.lock();
            // get track id
            int trkId = TableUtil.getTrackID(connection, artist, album, track);
            if (trkId == -1) {
                System.err.println("Track not found");
                return;
            }
            // check that a track exists with the new position
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM CHAIN_TRACK WHERE chn_id=? and trk_id=?");
            ps.setInt(1, chainId);
            ps.setInt(2, trkId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Track not found");
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
                System.err.println("Track position not found");
                return;
            }
            rs.next();
            int trkPos2 = rs.getInt("chn_trk_pos");
            int trkId2 = rs.getInt("trk_id");
            if (trkPos1 == trkPos2) {
                System.err.println("Track already at position specified");
                return;
            }
            int posDifference = trkPos2 - trkPos1; // this will be added to its playlist position
            ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos2);
            ps.setInt(2, trkId1);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.err.println("Track position not updated");
                return;
            }
            ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos1);
            ps.setInt(2, trkId2);
            int updated2 = ps.executeUpdate();
            if (updated2 == 0) {
                System.err.println("Track position not updated");
                return;
            }

            // update playlist position
            int playlistId = TableUtil.getPlaylistIdOfChain(connection, chainId);
            if (playlistId == -1) {
                System.err.println("Playlist not found");
                return;
            }
            int userId = TableUtil.getUserIdFromPlaylistId(connection, playlistId);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            String user = TableUtil.getUsernameFromId(connection, userId);
            if (user == null) {
                System.err.println("Username not found");
                return;
            }
            String playlist = TableUtil.getPlaylistNameFromId(connection, userId);
            if (playlist == null) {
                System.err.println("Playlist name not found");
                return;
            }
            ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
            ps.setInt(1, playlistId);
            ps.setInt(2, trkId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Playlist position not found");
                return;
            }
            rs.next();
            int playlistPos = rs.getInt("ply_trk_pos");
            playlistSwapPosition(connection, user, playlist, artist, album, track, playlistPos + posDifference);

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
            System.err.println("Track not found");
            return;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? and trk_id=?");
        ps.setInt(1, chainId);
        ps.setInt(2, trkId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Track position not found");
            return;
        }
        rs.next();
        int currPos = rs.getInt("chn_trk_pos");
        if (currPos == newPos) {
            System.err.println("Track already at position specified");
            return;
        }
        int posDifference = newPos - currPos; // this will be added to its playlist position
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
            System.err.println("Track position not found");
            return;
        }
        rs.next();
        // if newPos > currPos, decrement every track position between currPos and newPos (inclusive),
        // except track's trk_pos, which will become newPos
        // if newPos < currPos, increment every track position between currPos and newPos (inclusive)
        ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=? AND chn_trk_pos>=? AND chn_trk_pos<=?");
        ps.setInt(1, chainId);
        if (delta == -1) {
            ps.setInt(2, currPos);
            ps.setInt(3, newPos);
        }
        else {
            ps.setInt(2, newPos);
            ps.setInt(3, currPos);
        }
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Error obtaining track positions");
            return;
        }
        while (rs.next()) {
            int thisId = rs.getInt("trk_id");
            int thisPos = rs.getInt("chn_trk_pos");
            if (thisId != trkId) {
                ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE chn_id=? AND trk_id=?");
                int updatedPos = thisPos + delta;
                ps.setInt(1, updatedPos);
                ps.setInt(2, chainId);
                ps.setInt(3, thisId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.err.println("Track position update failed");
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
            System.err.println("Track position not updated");
            return;
        }

        // update playlist position
        int playlistId = TableUtil.getPlaylistIdOfChain(connection, chainId);
        if (playlistId == -1) {
            System.err.println("Playlist not found");
            return;
        }
        int userId = TableUtil.getUserIdFromPlaylistId(connection, playlistId);
        if (userId == -1) {
            System.err.println("User not found");
            return;
        }
        String user = TableUtil.getUsernameFromId(connection, userId);
        if (user == null) {
            System.err.println("Username not found");
            return;
        }
        String playlist = TableUtil.getPlaylistNameFromId(connection, userId);
        if (playlist == null) {
            System.err.println("Playlist name not found");
            return;
        }
        ps = connection.prepareStatement("SELECT ply_trk_pos FROM PLAYLIST_TRACK WHERE ply_id=? AND trk_id=?");
        ps.setInt(1, playlistId);
        ps.setInt(2, trkId);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Playlist position not found");
            return;
        }
        rs.next();
        int playlistPos = rs.getInt("ply_trk_pos");
        playlistInsertAtPosition(connection, user, playlist, artist, album, track, playlistPos + posDifference);

        System.out.println("Track positions updated");
        connection.commit();
    }


    public static ArrayList<String[]> getTableForChain(Connection connection, int chainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
        ps.setInt(1, chainId);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            System.err.println("Chain not found");
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