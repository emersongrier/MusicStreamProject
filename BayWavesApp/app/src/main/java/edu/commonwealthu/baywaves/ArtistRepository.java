package edu.commonwealthu.baywaves;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    private static ArtistRepository instance;
    private List<Artist> artists;
    private Connection connection;

    private ArtistRepository() {
        artists = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();

            // Fetch artists from database
            loadArtistsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            addDefaultArtists();
        }
    }

    public static synchronized ArtistRepository getInstance() {
        if (instance == null) {
            instance = new ArtistRepository();
        }
        return instance;
    }

    private void loadArtistsFromDatabase() throws SQLException {
        String query = "SELECT * FROM ARTIST";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Artist artist = new Artist(
                        rs.getInt("art_id"),
                        rs.getString("art_name"),
                        rs.getString("art_bio"),
                        rs.getInt("art_followers"),
                        rs.getInt("art_members")
                );
                artists.add(artist);
            }
        }

        // If no artists found, add default artists
        if (artists.isEmpty()) {
            addDefaultArtists();
        }
    }

    /**
     * Adds defaults artists to songs
     */
    private void addDefaultArtists() {
        artists.add(new Artist(
                1,
                "Aves",
                "Electronic music duo",
                5000,
                2
        ));

        artists.add(new Artist(
                2,
                "Yarin Primak",
                "Indie rock band",
                3000,
                4
        ));
        artists.add(new Artist(
                3,
                "Kevin MacLeod",
                "Good artist",
                0,
                0
        ));
    }

    public List<Artist> getAllArtists() {
        return new ArrayList<>(artists);
    }

    public Artist getArtistById(int id) {
        return artists.stream()
                .filter(artist -> artist.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void addArtist(Artist artist) {
        artists.add(artist);

        try {
            // Insert artist into database
            String query = "INSERT INTO ARTIST (art_id, art_name, art_bio, art_followers, art_members) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, artist.getId());
                ps.setString(2, artist.getName());
                ps.setString(3, artist.getBio());
                ps.setInt(4, artist.getFollowers());
                ps.setInt(5, artist.getMembers());

                ps.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateArtist(Artist artist) {
        // Update in local list
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).getId() == artist.getId()) {
                artists.set(i, artist);
                break;
            }
        }

        try {
            // Update in database
            String query = "UPDATE ARTIST SET " +
                    "art_name = ?, art_bio = ?, art_followers = ?, art_members = ? " +
                    "WHERE art_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, artist.getName());
                ps.setString(2, artist.getBio());
                ps.setInt(3, artist.getFollowers());
                ps.setInt(4, artist.getMembers());
                ps.setInt(5, artist.getId());

                ps.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteArtist(int artistId) {
        // Remove from local list
        artists.removeIf(artist -> artist.getId() == artistId);

        try {
            // Delete from database
            String query = "DELETE FROM ARTIST WHERE art_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, artistId);

                ps.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}