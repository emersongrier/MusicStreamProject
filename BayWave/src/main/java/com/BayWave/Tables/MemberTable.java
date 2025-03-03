package com.BayWave.Tables;

import com.BayWave.Reset;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MemberTable {
    /**
     * Prints the MEMBER table to output.
     */
    public static void print(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from MEMBER");
        ResultSet rs = ps.executeQuery();
        System.out.println("MEMBER TABLE:");
        TableUtil.print(rs);
    }

    /**
     * Adds a member to the MEMBER table.
     */
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

            // check if any other MEMBERs exist
            boolean firstMember = true;
            ps = connection.prepareStatement("SELECT * FROM MEMBER WHERE art_id=?");
            ps.setInt(1, artId);
            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                firstMember = false;
            }

            ps = connection.prepareStatement("INSERT INTO MEMBER (usr_id, art_id) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            ps.executeUpdate();

            if (firstMember) {
                ps = connection.prepareStatement("UPDATE MEMBER SET mbr_prim=TRUE WHERE art_id=? AND usr_id=?");
                ps.setInt(1, artId);
                ps.setInt(2, userId);
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Primary member status not updated");
                    return;
                }
            }

            connection.commit();
            System.out.println("MEMBER registered");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Removes a member from the MEMBER table. If that user is the primary member, another member of that
     * artist will be designated as primary member, should they exist. This member will be the first one
     * that appears in the ResultSet.
     */
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

            // check is member is primary member
            ps = connection.prepareStatement("SELECT mbr_prim FROM MEMBER WHERE usr_id=? AND art_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Member not found");
                return;
            }
            rs.next();
            boolean isPrimary = rs.getBoolean("mbr_prim");

            ps = connection.prepareStatement("DELETE FROM MEMBER WHERE usr_id=? AND art_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, artId);
            int count = ps.executeUpdate();
            if (count == 0) {
                System.out.println("Member not deleted");
            }

            if (isPrimary) {
                // if exists, set the first member in the ResultSet to the new primary member
                ps = connection.prepareStatement("SELECT usr_id FROM MEMBER WHERE art_id=?");
                ps.setInt(1, artId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int currUser = rs.getInt("usr_id");
                    ps = connection.prepareStatement("UPDATE MEMBER SET mbr_prim=TRUE WHERE art_id=? AND usr_id=?");
                    ps.setInt(1, artId);
                    ps.setInt(2, currUser);
                    ps.executeUpdate();
                }
            }

            connection.commit();
            System.out.println("Member deleted");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Sets a user as the primary member of an artist. If another user is
     * already primary member, that user's primary member status is revoked.
     */
    public static void setAsPrimary(Connection connection, String user, String artist) throws SQLException {
        try {
            Reset.lock.lock();
            int userId = TableUtil.getUserID(connection, user);
            if (userId == -1) {
                System.err.println("User not found");
                return;
            }
            int artistId = TableUtil.getArtistID(connection, artist);
            if (artistId == -1) {
                System.err.println("Artist not found");
                return;
            }
            boolean isPrimary;
            PreparedStatement ps = connection.prepareStatement("SELECT mbr_prim FROM MEMBER WHERE usr_id=? AND art_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, artistId);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.err.println("Member status not found");
                return;
            }
            rs.next();
            isPrimary = rs.getBoolean("mbr_prim");
            if (isPrimary) {
                System.err.println("User already primary member");
                return;
            }
            // remove primary status from any other members
            ps = connection.prepareStatement("UPDATE MEMBER SET mbr_prim=FALSE WHERE art_id=? AND usr_id!=?");
            ps.setInt(1, artistId);
            ps.setInt(2, userId);
            ps.executeUpdate();

            // add primary status to current member
            ps = connection.prepareStatement("UPDATE MEMBER SET mbr_prim=TRUE WHERE art_id=? AND usr_id=?");
            ps.setInt(1, artistId);
            ps.setInt(2, userId);
            int result = ps.executeUpdate();
            if (result == 0) {
                System.err.println("Primary member status not updated");
                return;
            }
            connection.commit();
            System.out.println("User set as primary member");
        }
        finally {
            Reset.lock.unlock();
        }
    }

    /**
     * Returns an ArrayList of String tables. Each string table represents a row in the MEMBER table,
     * except for the first one (at index 0 of the ArrayList), which is a header containing the attribute names.
     */
    public static ArrayList<String[]> getTable(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEMBER");
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        return TableUtil.getTable(rs);
    }
}
