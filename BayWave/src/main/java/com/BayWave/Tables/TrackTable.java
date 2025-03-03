package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrackTable {
    /**
     * Prints the TRACK table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from TRACK");
        ResultSet rs = ps.executeQuery();
        System.out.println("TRACK TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            if (track.isEmpty()) {
                System.out.println("Track name cannot be empty");
                return;
            }
            // get album id
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }

            // check if track in that album already exists
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE lower(trk_name)=lower(?) AND alb_id=?");
            ps.setString(1, track);
            ps.setInt(2, albId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Track already exists");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO TRACK (trk_name, alb_id) VALUES (?, ?)");
            ps.setString(1, track);
            ps.setInt(2, albId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not added");
                return;
            }
            System.out.println("Track added");
            connection.commit();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes a track, removing it from its album.
     */
    public static void delete(Connection connection, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            if (track.isEmpty()) {
                System.out.println("Track name cannot be empty");
                return;
            }
            // get album id
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM TRACK WHERE lower(trk_name)=lower(?) AND alb_id=?");
            ps.setString(1, track);
            ps.setInt(2, albId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not deleted");
            }
            System.out.println("Track deleted");
            connection.commit();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateName(Connection connection, String artist, String album, String trkName, String newName) throws SQLException {
        try {
            Reset.lock.lock();
            // get album ID
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            // check if track name already exists for that album
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM TRACK WHERE LOWER(trk_name)=LOWER(?) AND alb_id=?");
            ps.setString(1, newName);
            ps.setInt(2, albId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Track name already exists for album, not updated");
                return;
            }
            // track name available
            ps = connection.prepareStatement(
                    "UPDATE TRACK SET trk_name=? WHERE LOWER(trk_name)=LOWER(?) AND alb_id=?");
            ps.setString(1, newName);
            ps.setString(2, trkName);
            ps.setInt(3, albId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Track name not updated");
                return;
            }
            connection.commit();
            System.out.println("Track name updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateFile(Connection connection, String artist, String album, String trkName, String fileName) throws SQLException {
        try {
            Reset.lock.lock();
            // TODO: Check if filepath is valid
            // get album ID
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            // track name available
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE TRACK SET trk_file=? WHERE LOWER(trk_name)=LOWER(?) AND alb_id=?");
            ps.setString(1, fileName);
            ps.setString(2, trkName);
            ps.setInt(3, albId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Filepath not updated");
                return;
            }
            connection.commit();
            System.out.println("Filepath updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void updateLyrics(Connection connection, String artist, String album, String trkName, String fileName) throws SQLException {
        try {
            Reset.lock.lock();
            // TODO: Check if filepath is valid
            // get album ID
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            // track name available
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE TRACK SET trk_lyrics=? WHERE LOWER(trk_name)=LOWER(?) AND alb_id=?");
            ps.setString(1, fileName);
            ps.setString(2, trkName);
            ps.setInt(3, albId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Filepath not updated");
                return;
            }
            connection.commit();
            System.out.println("Filepath updated");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void printLyrics(Connection connection, String artist, String album, String trkName) throws SQLException {
        try {
            Reset.lock.lock();
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT trk_lyrics FROM TRACK WHERE alb_id=? AND lower(trk_name)=LOWER(?)");
            ps.setInt(1, albId);
            ps.setString(2, trkName);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track lyrics not found");
                return;
            }
            rs.next();
            String filepath = rs.getString(1);
            if (filepath == null) {
                System.out.println("Filepath is null");
                return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void swapPosition(Connection connection, String artist, String album, String name, int newPos) throws SQLException {
        try {
            Reset.lock.lock();
            // check that a track exists with the new position
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM TRACK WHERE alb_id=? and LOWER(trk_name)=LOWER(?)");
            ps.setInt(1, albId);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkPos1 = rs.getInt("trk_pos");
            int trkId1 = rs.getInt("trk_id");
            ps = connection.prepareStatement(
                    "SELECT * FROM TRACK WHERE alb_id=? AND trk_pos=?");
            ps.setInt(1, albId);
            ps.setInt(2, newPos);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track position not found");
                return;
            }
            rs.next();
            int trkPos2 = rs.getInt("trk_pos");
            int trkId2 = rs.getInt("trk_id");
            if (trkPos1 == trkPos2) {
                System.out.println("Track already at position specified");
                return;
            }
            ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos2);
            ps.setInt(2, trkId1);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Track position not updated");
                return;
            }
            ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
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

    public static void insertAtPosition(Connection connection, String artist, String album, String track, int newPos) throws SQLException {
        int albId = TableUtil.getAlbumID(connection, artist, album);
        if (albId == -1) {
            System.out.println("Album not found");
            return;
        }
        // make sure track exists
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND lower(trk_name)=lower(?)");
        ps.setInt(1, albId);
        ps.setString(2, track);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track not found");
            return;
        }
        rs.next();
        int trkId = rs.getInt("trk_id");
        if (trkId == -1) {
            System.out.println("Track not found");
            return;
        }
        int currPos = rs.getInt("trk_pos");
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
        ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND trk_pos=?");
        ps.setInt(1, albId);
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
        ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND trk_pos>=? AND trk_pos<=?");
        ps.setInt(1, albId);
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
            int thisPos = rs.getInt("trk_pos");
            if (thisId != trkId) {
                System.out.println("UPDATING TRACK");
                ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
                int updatedPos = thisPos + delta;
                ps.setInt(1, updatedPos);
                ps.setInt(2, thisId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Track position update failed");
                    return;
                }
            }
        }
        // set track's trk_pos to newPos
        ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
        ps.setInt(1, newPos);
        ps.setInt(2, trkId);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("Track position not updated");
            return;
        }
        System.out.println("Track positions updated");
        connection.commit();
    }

    public static String[] getTrack(Connection connection, String artist, String album, String track) throws SQLException {
        int trackId = TableUtil.getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            return null;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE trk_id=?");
        ps.setInt(1, trackId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Track not found");
            return null;
        }
        return TableUtil.getFirstStringTable(rs);
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the TRACK table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
