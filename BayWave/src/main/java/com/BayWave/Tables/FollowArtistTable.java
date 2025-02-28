package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FollowArtistTable {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from FOLLOW_ARTIST");
        ResultSet rs = ps.executeQuery();
        System.out.println("FOLLOW_ARTIST TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String user, String artist) throws SQLException {
        try {
            Reset.lock.lock();
            // obtain art_id and usr_id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            int usrId = TableUtil.getUserID(connection, user);
            if (usrId == -1) {
                System.out.println("User not found");
                return;
            }

            // check if FOLLOW_ARTIST already exist

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FOLLOW_ARTIST WHERE usr_id=? AND art_id=?");
            ps.setInt(1, usrId);
            ps.setInt(2, artId);
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("FOLLOW_ARTIST already exists");
                return;
            }

            ps = connection.prepareStatement("INSERT INTO FOLLOW_ARTIST (usr_id, art_id) VALUES (?, ?)");
            ps.setInt(1, usrId);
            ps.setInt(2, artId);
            ps.executeUpdate();
            connection.commit();
            System.out.println("FOLLOW_ARTIST registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, String user, String artist) throws SQLException {
        try {
            Reset.lock.lock();
            // obtain art_id and usr_id
            int artId = TableUtil.getArtistID(connection, artist);
            if (artId == -1) {
                System.out.println("Artist not found");
                return;
            }
            int usrId = TableUtil.getUserID(connection, user);
            if (usrId == -1) {
                System.out.println("User not found");
                return;
            }

            PreparedStatement ps = connection.prepareStatement("DELETE FROM FOLLOW_ARTIST WHERE usr_id=? AND art_id=?");
            ps.setInt(1, usrId);
            ps.setInt(2, artId);
            int count = ps.executeUpdate();
            if (count == 0) {
                System.out.println("FOLLOW_ARTIST not deleted");
            }
            connection.commit();
            System.out.println("FOLLOW_ARTIST deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FOLLOW_ARTIST");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
