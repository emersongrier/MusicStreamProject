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


    /**
     * Creates a lists of artists.
     * Fetches artist from database if not null, otherwise uses the default artists
     */
    private ArtistRepository() {
        artists = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            loadArtistsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            addDefaultArtists();
        }
    }

    /**
     * Creates an instance of the ArtistRepository class
     * @return an ArtistRepository object
     */
    public static synchronized ArtistRepository getInstance() {
        if (instance == null) {
            instance = new ArtistRepository();
        }
        return instance;
    }


    /**
     * Adds artists to list from database.
     * If no artists are found, it adds the default artists
     */
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

        if (artists.isEmpty()) {
            addDefaultArtists();
        }
    }

    /**
     * Sets a list of default artists as a backup
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

    /**
     * Returns all artists
     * @return a list of all artists
     */
    public List<Artist> getAllArtists() {
        return new ArrayList<>(artists);
    }



    /**
     * Retrieves an artist by their unique identifier.
     * @param id the unique identifier of the artist to retrieve
     * @return the Artist object with the specified ID, or null if no artist is found
     */
    public Artist getArtistById(int id) {
        return artists.stream()
                .filter(artist -> artist.getId() == id)
                .findFirst()
                .orElse(null);
    }


    /**
     * Adds artist to the database if user wanted to upload own music (not currently a feature)
     * @param artist The artist being added to the database
     */
    public void addArtist(Artist artist) {
        artists.add(artist);

        try {
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

    /**
     * Updates artist's info in database
     * @param artist The artist being updated
     */
    public void updateArtist(Artist artist) {
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).getId() == artist.getId()) {
                artists.set(i, artist);
                break;
            }
        }

        try {
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

    /**
     * Deletes an artist from the database
     * @param artistId The Id of the artist being deleted
     */
    public void deleteArtist(int artistId) {
        artists.removeIf(artist -> artist.getId() == artistId); // remove from local list

        try {
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