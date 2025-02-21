package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueueTrackTable {
    public static void register(Connection connection, String user, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            int albId = TableUtil.getAlbumID(connection, artist, album);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND LOWER(trk_name)=LOWER(?)");
            ps.setInt(1, albId);
            ps.setString(2, track);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkId = rs.getInt("trk_id");
            int usrId = TableUtil.getUserID(connection, user);
            // get queue ID
            ps = connection.prepareStatement("SELECT * FROM QUEUE WHERE usr_id=?");
            ps.setInt(1, usrId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Queue not found");
            }
            rs.next();
            int queId = rs.getInt("que_id");
            ps = connection.prepareStatement("INSERT INTO QUEUE_TRACK (que_id, trk_id) VALUES (?, ?)");
            ps.setInt(1, queId);
            ps.setInt(2, trkId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Queue insert failed");
            }
            connection.commit();
            System.out.println("Track added to queue");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, String user, String artist, String album, String track) throws SQLException {
        try {
            Reset.lock.lock();
            if (track.isEmpty()) {
                System.out.println("Track name cannot be empty");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            // get queue id
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE WHERE usr_id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Queue not found");
                return;
            }
            rs.next();
            int queId = rs.getInt("que_id");

            // get album id
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }

            // get track id
            ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND lower(trk_name)=lower(?)");
            ps.setInt(1, albId);
            ps.setString(2, track);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkId = rs.getInt("trk_id");

            ps = connection.prepareStatement("DELETE FROM QUEUE_TRACK WHERE que_id=? AND trk_id=?");
            ps.setInt(1, queId);
            ps.setInt(2, trkId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.out.println("Track not deleted");
                return;
            }
            System.out.println("Track deleted from queue");
            connection.commit();
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void swapPosition(Connection connection, String artist, String album, String track, String user, int newPos) throws SQLException {
        try {
            Reset.lock.lock();
            int albId = TableUtil.getAlbumID(connection, artist, album);
            if (albId == -1) {
                System.out.println("Album not found");
                return;
            }
            int userId = TableUtil.getUserID(connection, user);
            // get queue id
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE WHERE usr_id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Queue not found");
                return;
            }
            rs.next();
            int queId = rs.getInt("que_id");
            // get track id
            ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND LOWER(trk_name)=lower(?)");
            ps.setInt(1, albId);
            ps.setString(2, track);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkId = rs.getInt("trk_id");
            // check that a track exists with the new position
            ps = connection.prepareStatement(
                    "SELECT * FROM QUEUE_TRACK WHERE que_id=? and trk_id=?");
            ps.setInt(1, queId);
            ps.setInt(2, trkId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track not found");
                return;
            }
            rs.next();
            int trkPos1 = rs.getInt("que_trk_pos");
            int trkId1 = rs.getInt("trk_id");
            ps = connection.prepareStatement(
                    "SELECT * FROM QUEUE_TRACK WHERE que_id=? AND que_trk_pos=?");
            ps.setInt(1, queId);
            ps.setInt(2, newPos);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Track position not found");
                return;
            }
            rs.next();
            int trkPos2 = rs.getInt("que_trk_pos");
            int trkId2 = rs.getInt("trk_id");
            ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE trk_id=?");
            ps.setInt(1, trkPos2);
            ps.setInt(2, trkId1);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                System.out.println("Track position not updated");
                return;
            }
            ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE trk_id=?");
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

    public static void insertAtPosition(Connection connection, String artist, String album, String track, String user, int newPos) throws SQLException {
        int albId = TableUtil.getAlbumID(connection, artist, album);
        if (albId == -1) {
            System.out.println("Album not found");
            return;
        }
        int userId = TableUtil.getUserID(connection, user);
        // get queue id
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE WHERE usr_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Queue not found");
            return;
        }
        rs.next();
        int queId = rs.getInt("que_id");
        // get track id
        ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND LOWER(trk_name)=lower(?)");
        ps.setInt(1, albId);
        ps.setString(2, track);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track not found");
            return;
        }
        rs.next();
        int trkId = rs.getInt("trk_id");
        ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK WHERE que_id=? and trk_id=?");
        ps.setInt(1, queId);
        ps.setInt(2, trkId);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Track position not found");
        }
        rs.next();
        int currPos = rs.getInt("que_trk_pos");
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
        ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK WHERE que_id=? AND que_trk_pos=?");
        ps.setInt(1, queId);
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
        ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK WHERE que_id=? AND que_trk_pos>=? AND que_trk_pos<=?");
        ps.setInt(1, queId);
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
            int thisPos = rs.getInt("que_trk_pos");
            if (thisId != trkId) {
                System.out.println("UPDATING TRACK");
                ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE que_id=? AND trk_id=?");
                int updatedPos = thisPos + delta;
                ps.setInt(1, updatedPos);
                ps.setInt(2, queId);
                ps.setInt(3, thisId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Track position update failed");
                    return;
                }
            }
        }
        // set track's trk_pos to newPos
        ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE que_id=? AND trk_id=?");
        ps.setInt(1, newPos);
        ps.setInt(2, queId);
        ps.setInt(3, trkId);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("Track position not updated");
            return;
        }
        System.out.println("Track positions updated");
        connection.commit();
    }

    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK"); // select * from USERS_
        ResultSet rs = ps.executeQuery();
        System.out.println("QUEUE_TRACK TABLE:");
        TableUtil.print(rs);
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }

    public static ArrayList<String[]> getTableForUser(Connection connection, String user) throws SQLException {
        int userId = TableUtil.getUserID(connection, user);
        // get queue id
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE WHERE usr_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Queue not found");
            return null;
        }
        rs.next();
        int queId = rs.getInt("que_id");
        ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK WHERE que_id=?");
        ps.setInt(1, queId);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
