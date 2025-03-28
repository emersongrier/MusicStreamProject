package edu.commonwealthu.baywaves;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository {
    private static TrackRepository instance;

    private TrackRepository() {
        // Private constructor to prevent direct instantiation
    }


    public static synchronized TrackRepository getInstance() {
        if (instance == null) {
            instance = new TrackRepository();
        }
        return instance;
    }



    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Track track = new Track(
                        rs.getInt("trk_id"),
                        rs.getString("trk_name"),
                        rs.getString("trk_file"),
                        rs.getInt("trk_pos"),
                        rs.getString("trk_lyrics"),
                        rs.getInt("trk_len"),
                        rs.getInt("trk_strms"),
                        rs.getInt("trk_likes"),
                        rs.getInt("alb_id")
                );
                tracks.add(track);
            }
        } catch (SQLException e) {
            // Log the error or handle it appropriately
            e.printStackTrace();
            // Fallback to local tracks if database connection fails
            tracks = getLocalFallbackTracks();
        }
        return tracks;
    }

    public Track getTrackById(int id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE trk_id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Track(
                        rs.getInt("trk_id"),
                        rs.getString("trk_name"),
                        rs.getString("trk_file"),
                        rs.getInt("trk_pos"),
                        rs.getString("trk_lyrics"),
                        rs.getInt("trk_len"),
                        rs.getInt("trk_strms"),
                        rs.getInt("trk_likes"),
                        rs.getInt("alb_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTrack(Track track) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO TRACK (trk_name, trk_file, trk_pos, trk_lyrics, trk_len, trk_strms, trk_likes, alb_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, track.getName());
            ps.setString(2, track.getFilePath());
            ps.setInt(3, track.getPosition());
            ps.setString(4, track.getLyrics());
            ps.setInt(5, track.getLength());
            ps.setInt(6, track.getStreams());
            ps.setInt(7, track.getLikes());
            ps.setInt(8, track.getAlbumId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void updateTrack(Track track) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE TRACK SET trk_name=?, trk_file=?, trk_pos=?, trk_lyrics=?, " +
                             "trk_len=?, trk_strms=?, trk_likes=?, alb_id=? WHERE trk_id=?")) {

            ps.setString(1, track.getName());
            ps.setString(2, track.getFilePath());
            ps.setInt(3, track.getPosition());
            ps.setString(4, track.getLyrics());
            ps.setInt(5, track.getLength());
            ps.setInt(6, track.getStreams());
            ps.setInt(7, track.getLikes());
            ps.setInt(8, track.getAlbumId());
            ps.setInt(9, track.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fallback method to provide local tracks if database connection fails
    private List<Track> getLocalFallbackTracks() {
        List<Track> fallbackTracks = new ArrayList<>();
        fallbackTracks.add(new Track(
                1,
                "Rollin",
                "android.resource://edu.commonwealthu.baywaves/" + R.raw.aves_rollin,
                1,
                "Sample lyrics for Aves Rollin",
                0,
                1000,
                0,
                1
        ));
        fallbackTracks.add(new Track(
                2,
                "Special Vibe",
                "android.resource://edu.commonwealthu.baywaves/" + R.raw.special_vibe,
                2,
                "Lyrics for Special Vibe",
                0,
                500,
                0,
                2
        ));
        return fallbackTracks;
    }

    public boolean isDatabaseConnected() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            // Log the specific connection error
            Log.e("DatabaseConnection", "Connection failed: " + e.getMessage());
            return false;
        }
    }

    public String getConnectionErrorMessage() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return "Database Connected Successfully!";
        } catch (SQLException e) {
            return "Database Connection Failed: " + e.getMessage();
        }
    }
}