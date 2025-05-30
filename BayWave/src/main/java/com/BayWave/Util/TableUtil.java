package com.BayWave.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class TableUtil {
    public static void print(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            if (i == 1) {
                System.out.format("%-8s", rsmd.getColumnName(i));
            }
            else {
                System.out.format("%-20s", rsmd.getColumnName(i));
            }
            if (i != cols) {
                System.out.print(" | ");
            }
        }
        System.out.println();
        for (int i = 1; i <= cols; i++) {
            if (i == 1) {
                for (int j = 1; j <= 11; j++) {
                    System.out.print("─");
                }
            }
            else {
                for (int j = 1; j <= 23; j++) {
                    System.out.print("─");
                }
            }
        }
        System.out.println();

        while (rs.next()) {
            for (int i = 1; i <= cols; i++) {
                if (i == 1) {
                    if (rs.getString(i).length() > 20) {
                        System.out.format("%-8s", rs.getString(i).substring(0, 5) + "...");
                    }
                    else {
                        System.out.format("%-8s", rs.getString(i));
                    }
                }
                else if (rs.getString(i) != null) {
                    if (rs.getString(i).length() > 20) {
                        System.out.format("%-20s", rs.getString(i).substring(0, 17) + "...");
                    }
                    else {
                        System.out.format("%-20s", rs.getString(i));
                    }
                }
                else {
                    System.out.format("%-20s", "NULL");
                }
                if (i != cols) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }

    }

    public static int getUserID(Connection connection, String user) throws SQLException {
        // get userId from name
        PreparedStatement ps = connection.prepareStatement(
                "SELECT usr_id from USER_ WHERE LOWER(usr_name)=LOWER(?)");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return -1; // indicates user ID could not be found
        }
        rs.next();
        return rs.getInt(1);
    }

    public static int getArtistID(Connection connection, String artist) throws SQLException {
        // get userId from name
        PreparedStatement ps = connection.prepareStatement(
                "SELECT art_id from ARTIST WHERE LOWER(art_name)=LOWER(?)");
        ps.setString(1, artist);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return -1; // indicates user ID could not be found
        }
        rs.next();
        return rs.getInt(1);
    }

    public static int getAlbumID(Connection connection, String artist, String album) throws SQLException {
        // get artist id
        int artId = getArtistID(connection, artist);
        if (artId == -1) {
            System.out.println("Artist not found");
            return -1;
        }
        // get userId from name
        PreparedStatement ps = connection.prepareStatement(
                "SELECT alb_id from ALBUM WHERE LOWER(alb_name)=LOWER(?) AND art_id=?");
        ps.setString(1, album);
        ps.setInt(2, artId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return -1; // indicates user ID could not be found
        }
        rs.next();
        return rs.getInt(1);
    }

    public static int getPlaylistID(Connection connection, String user, String playlist) throws SQLException {
        int userID = getUserID(connection, user);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE usr_id=? AND LOWER(ply_name)=LOWER(?)");
        ps.setInt(1, userID);
        ps.setString(2, playlist);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Playlist not found");
            return -1;
        }
        rs.next();
        int playlistID = rs.getInt(1);
        if (playlistID == -1) {
            System.out.println("Playlist ID not found");
            return -1;
        }
        return playlistID;
    }

    public static int getTrackID(Connection connection, String artist, String album, String track) throws SQLException {
        int albId = getAlbumID(connection, artist, album);
        if (albId == -1) {
            System.out.println("Album not found");
            return -1;
        }
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=? AND LOWER(trk_name)=LOWER(?)");
        ps.setInt(1, albId);
        ps.setString(2, track);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            return -1;
        }
        rs.next();
        int trkId = rs.getInt(1);
        if (trkId == -1) {
            System.out.println("Track not found");
            return -1;
        }
        return trkId;
    }

    public static int getPlaylistIdOfChain(Connection connection, int chainId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ply_id FROM CHAIN_ WHERE chn_id=?");
        ps.setInt(1, chainId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Playlist ID not found given chain");
            return -1;
        }
        rs.next();
        return rs.getInt("ply_id");
    }

    public static String getPlaylistNameFromId(Connection connection, int playlistId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Playlist not found");
            return null;
        }
        rs.next();
        return rs.getString("ply_name");
    }

    public static String getUsernameFromId(Connection connection, int userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER_ WHERE usr_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Playlist not found");
            return null;
        }
        rs.next();
        return rs.getString("usr_name");
    }

    public static int getUserIdFromPlaylistId(Connection connection, int playlistId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT usr_id FROM PLAYLIST WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("User ID not found given playlist");
            return -1;
        }
        rs.next();
        return rs.getInt("usr_id");
    }

    public static int getChainPos(Connection connection, int playlistId, int trackId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Chain not found");
            return -1;
        }
        while (rs.next()) {
            ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
            ps.setInt(1, rs.getInt("chn_id"));
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                if (rs2.getInt("trk_id") == trackId) {
                    return rs2.getInt("chn_trk_pos");
                }
            }
        }
        return -1;
    }

    public static int getChainPos(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        int playlistId = getPlaylistID(connection, user, playlist);
        if (playlistId == -1) {
            System.err.println("Playlist not found");
            return -1;
        }

        int trackId = getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            System.err.println("Track not found");
            return -1;
        }
        return getChainPos(connection, playlistId, trackId);
    }

    public static int getChainID(Connection connection, int playlistId, int trackId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_ WHERE ply_id=?");
        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.err.println("Chain not found");
            return -1;
        }
        while (rs.next()) {
            ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
            ps.setInt(1, rs.getInt("chn_id"));
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                if (rs2.getInt("trk_id") == trackId) {
                    return rs2.getInt("chn_id");
                }
            }
        }
        return -1;
    }

    public static int getChainID(Connection connection, String user, String playlist, String artist, String album, String track) throws SQLException {
        int playlistId = getPlaylistID(connection, user, playlist);
        if (playlistId == -1) {
            System.err.println("Playlist not found");
            return -1;
        }
        int trackId = getTrackID(connection, artist, album, track);
        if (trackId == -1) {
            System.err.println("Track not found");
            return -1;
        }
        return getChainID(connection, playlistId, trackId);
    }

    public static int getGenreID(Connection connection, String genre) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT gen_id FROM GENRE WHERE LOWER(gen_name)=LOWER(?)");
        ps.setString(1, genre);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("Genre not found");
            return -1;
        }
        rs.next();
        return rs.getInt("gen_id");
    }

    public static boolean isValidType(String type) {
        return Objects.equals(type, "Single") || Objects.equals(type, "EP") || Objects.equals(type, "LP");
    }

    public static boolean isValidEmbed(String type) {
        return Objects.equals(type, "Artist") || Objects.equals(type, "Album") ||
                Objects.equals(type, "Track") || Objects.equals(type, "Playlist");
    }

    public static boolean isValidPost(Connection connection, int postId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM POST WHERE pst_id=?");
        ps.setInt(1, postId);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public static boolean embedExists(Connection connection, String embedType, int embedId) throws SQLException {
        if (Objects.equals(embedType, "Artist")) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ARTIST WHERE art_id=?");
            ps.setInt(1, embedId);
            ResultSet rs = ps.executeQuery();
            return rs.isBeforeFirst();
        }
        else if (Objects.equals(embedType, "Album")) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ALBUM WHERE alb_id=?");
            ps.setInt(1, embedId);
            ResultSet rs = ps.executeQuery();
            return rs.isBeforeFirst();
        }
        else if (Objects.equals(embedType, "Track")) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE trk_id=?");
            ps.setInt(1, embedId);
            ResultSet rs = ps.executeQuery();
            return rs.isBeforeFirst();
        }
        else if (Objects.equals(embedType, "Playlist")) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST WHERE ply_id=?");
            ps.setInt(1, embedId);
            ResultSet rs = ps.executeQuery();
            return rs.isBeforeFirst();
        }
        return false;
    }

    public static ArrayList<String[]> getTable(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            return null;
        }
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        ArrayList<String[]> data = new ArrayList<>();
        String[] header = new String[cols];
        for (int i = 0; i < cols; i++) {
            header[i] = rsmd.getColumnLabel(i + 1);
        }
        data.add(header);
        while (rs.next()) {
            String[] elem = new String[cols];
            for (int i = 0; i < cols; i++) {
                elem[i] = rs.getString(i + 1);
            }
            data.add(elem);
        }
        return data;
    }

    /**
     * Returns the first line of a ResultSet as a String table.
     */
    public static String[] getFirstStringTable(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            return null;
        }
        rs.next();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        String[] data = new String[cols];
        for (int i = 0; i < cols; i++) {
            data[i] = rs.getString(i + 1);
        }
        return data;
    }

    /**
     * Returns the first column of a result set as a string table. The second column
     * should represent the amount of rows in the ResultSet.
     */
    public static String[] getFirstColumnStringTable(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            return null;
        }
        rs.next();
        int rows = rs.getInt(2);
        String[] data = new String[rows];
        data[0] = rs.getString(1);
        for (int i = 1; i < rows; i++) {
            rs.next();
            data[i] = rs.getString(i);
        }
        return data;
    }

    public static void printArrayList(ArrayList<String[]> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                if (i == 0) {
                    if (j == 0) {
                        System.out.format("%-8s", list.get(i)[j]);
                    } else {
                        System.out.format("%-20s", list.get(i)[j]);
                    }
                }
                else if (list.get(i)[j] != null) {
                    if (j == 0) {
                        System.out.format("%-8s", list.get(i)[j]);
                    }
                    else if (list.get(i)[j].length() > 20) {
                        System.out.format("%-20s", list.get(i)[j].substring(0, 17) + "...");
                    }
                    else {
                        System.out.format("%-20s", list.get(i)[j]);
                    }
                }
                else {
                    System.out.format("%-20s", "NULL");
                }
                if (j != list.get(i).length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (i == 0) {
                for (int l = 1; l <= list.get(i).length; l++) {
                    if (l == 1) {
                        for (int m = 1; m <= 11; m++) {
                            System.out.print("─");
                        }
                    }
                    else {
                        for (int m = 1; m <= 23; m++) {
                            System.out.print("─");
                        }
                    }
                }
                System.out.println();
            }
        }
    }

    public static void printStringTable(String[] table) {
        for (int i = 0; i < table.length; i++) {
            System.out.print(table[i]);
            if (i != table.length - 1) {
                System.out.print(", ");
            }
        }
    }
}
