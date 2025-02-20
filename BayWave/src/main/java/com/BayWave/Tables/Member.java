package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Member {
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from MEMBER");
        ResultSet rs = ps.executeQuery();
        System.out.println("MEMBER TABLE:");
        TableUtil.print(rs);
    }

    public static void register(Connection connection, String user, String artist) throws SQLException {
        try {
            Reset.lock.lock();
            // obtain usr_id and art_id
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            int artId;
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT art_id FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, artist);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                artId = rs.getInt("art_id");
            } else {
                System.out.println("Artist not found");
                return;
            }

            // check if MEMBER already exist

            ps = connection.prepareStatement("SELECT * FROM MEMBER WHERE usr_id=? AND art_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("MEMBER already exists");
                return;
            }
            ps = connection.prepareStatement("INSERT INTO MEMBER (usr_id, art_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            ps.executeUpdate();
            connection.commit();
            System.out.println("MEMBER registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static void delete(Connection connection, String user, String artist) throws SQLException {
        try {
            Reset.lock.lock();
            // obtain usr_id and art_id
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.out.println("User not found");
                return;
            }
            int artId;
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT art_id FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, artist);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                artId = rs.getInt("art_id");
            } else {
                System.out.println("Artist not found");
                return;
            }
            ps = connection.prepareStatement(
                    "SELECT art_id FROM ARTIST WHERE LOWER(art_name)=LOWER(?)");
            ps.setString(1, artist);
            rs = ps.executeQuery();
            if (rs.next()) {
                artId = rs.getInt("art_id");
            } else {
                System.out.println("Artist not found");
                return;
            }

            ps = connection.prepareStatement("DELETE FROM MEMBER WHERE usr_id=? AND art_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            int count = ps.executeUpdate();
            if (count == 0) {
                System.out.println("Member not deleted");
            }
            connection.commit();
            System.out.println("Member deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEMBER");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
