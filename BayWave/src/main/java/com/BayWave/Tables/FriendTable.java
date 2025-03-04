package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FriendTable {
    /**
     * Prints the FRIEND table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from FRIEND");
        ResultSet rs = ps.executeQuery();
        System.out.println("FRIEND TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Creates a friendship between two users.
     */
    public static void register(Connection connection, String friend1, String friend2) throws SQLException {
        try {
            Reset.lock.lock();
            if (friend1.equalsIgnoreCase(friend2)) {
                System.out.println("Users cannot friend themselves");
                return;
            }

            // obtain usr_id for each
            int friendId1;
            int friendId2;
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT usr_id FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, friend1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                friendId1 = rs.getInt("usr_id");
            } else {
                System.out.println("Friend 1 not found");
                return;
            }
            ps = connection.prepareStatement(
                    "SELECT usr_id FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, friend2);
            rs = ps.executeQuery();
            if (rs.next()) {
                friendId2 = rs.getInt("usr_id");
            } else {
                System.out.println("Friend 2 not found");
                return;
            }

            // check if friends already exist

            ps = connection.prepareStatement("SELECT * FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
            ps.setInt(1, friendId1);
            ps.setInt(2, friendId2);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Friendship already exists");
                return;
            }

            // swapping places
            ps = connection.prepareStatement("SELECT * FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
            ps.setInt(1, friendId2);
            ps.setInt(2, friendId1);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                System.out.println("Friendship already exists");
                return;
            }

            // friendship doesn't exist, create friendship

            ps = connection.prepareStatement("INSERT INTO FRIEND (usr_id1, usr_id2) VALUES (?, ?)");
            ps.setInt(1, friendId1);
            ps.setInt(2, friendId2);
            ps.executeUpdate();
            connection.commit();
            System.out.println("Friends registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Deletes the friendship between the two specified users.
     */
    public static void delete(Connection connection, String friend1, String friend2) throws SQLException {
        try {
            Reset.lock.lock();
            if (friend1.equalsIgnoreCase(friend2)) {
                System.out.println("Friendship does not exist");
                return;
            }

            // obtain usr_id for each
            int friendId1;
            int friendId2;
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT usr_id FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, friend1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                friendId1 = rs.getInt("usr_id");
            } else {
                System.out.println("Friend 1 not found");
                return;
            }
            ps = connection.prepareStatement(
                    "SELECT usr_id FROM USER_ WHERE LOWER(usr_name)=LOWER(?)");
            ps.setString(1, friend2);
            rs = ps.executeQuery();
            if (rs.next()) {
                friendId2 = rs.getInt("usr_id");
            } else {
                System.out.println("Friend 2 not found");
                return;
            }

            // check if friends already exist

            ps = connection.prepareStatement("SELECT * FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
            ps.setInt(1, friendId1);
            ps.setInt(2, friendId2);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                ps = connection.prepareStatement("DELETE FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
                ps.setInt(1, friendId1);
                ps.setInt(2, friendId2);
                ps.executeUpdate();
                connection.commit();
                System.out.println("Friendship deleted");
                return;
            }

            // swapping places
            ps = connection.prepareStatement("SELECT * FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
            ps.setInt(1, friendId2);
            ps.setInt(2, friendId1);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                ps = connection.prepareStatement("DELETE FROM FRIEND WHERE usr_id1=? AND usr_id2=?");
                ps.setInt(1, friendId2);
                ps.setInt(2, friendId1);
                ps.executeUpdate();
                connection.commit();
                System.out.println("Friendship deleted");
                return;
            }
            System.out.println("Friendship does not exist");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the FRIEND table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM FRIEND");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
