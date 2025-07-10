package edu.commonwealthu.baywaves;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves as a repository for establishing an album and getting the correct data
 * corresponding to the album. This implementation uses the old system of connecting to the
 * database instead of accessing it through the server, since not all song metadata can be received
 * from the server at this time.
 *
 * Author: Jacob Leonardo
 */

public class AlbumRepository {
    private static AlbumRepository instance;
    private List<Album> albums;
    private Connection connection;
    private final Map<Integer, Integer> albumCoverResourceIds = new HashMap<>(); // Store resource IDs instead of ImageViews
    private TrackRepository trackRepository;
    private ArtistRepository artistRepository;


    /**
     * Fetches albums from database.
     * If database connection fails, use default albums
     */
    private AlbumRepository() {
        albums = new ArrayList<>();
        trackRepository = TrackRepository.getInstance();
        artistRepository = ArtistRepository.getInstance();

        try {
            connection = DatabaseConnection.getConnection();
            loadAlbumsFromDatabase();
        } catch (Exception e) {
            addDefaultAlbums();
        }
    }


    /**
     * Returns an instance of the Album Repository
     * @return an instance of the Album Repository
     */
    public static synchronized AlbumRepository getInstance() {
        if (instance == null) {
            instance = new AlbumRepository();
        }
        return instance;
    }


    /**
     * Originally tries to load album from database before database only became available through the server.
     * Server currently doesn't contain albums information, but shows how to update database if not accessible
     * through server.
     * Sets an album and dynamically updates its info.
     * If non are found, refers to the default albums (always does this since albums are not on server)
     */
    private void loadAlbumsFromDatabase() throws SQLException {
        if (connection == null) {
            addDefaultAlbums();
            return;
        }

        String query = "SELECT * FROM ALBUM";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int albumId = rs.getInt("alb_id");
                String type = rs.getString("alb_type");
                String name = rs.getString("alb_name");
                int likes = rs.getInt("alb_likes");
                int artistId = rs.getInt("art_id");

                List<Track> albumTracks = trackRepository.getTracksByAlbum(albumId);

                // Create album with null cover for now (will be set later)
                Album album = new Album(
                        albumId,
                        type,
                        name,
                        null, // Cover will be set dynamically
                        albumTracks,
                        likes,
                        artistId
                );
                albums.add(album);
            }
        }

        if (albums.isEmpty()) {
            addDefaultAlbums();
        }
    }


    /**
     * Sets a list of default albums as a backup
     */
    private void addDefaultAlbums() {

        Album avesAlbum = new Album(
                1,
                "Album",
                "Waves",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                200,
                1 // Artist ID for Aves
        );
        albums.add(avesAlbum);

        // Add default album for Yarin Primak
        Album yarinAlbum = new Album(
                2,
                "Album",
                "Special Collection",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                150,
                2 // Artist ID for Yarin Primak
        );
        albums.add(yarinAlbum);

        // Add default album for Aves
        Album fonkyThings = new Album(
                3,
                "Album",
                "The Fonky Things",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                150,
                1 // Artist ID for aves
        );
        albums.add(fonkyThings);

        // Add default album for Aves
        Album missing_Hits_C_to_E = new Album(
                4,
                "Album",
                "Missing Hits C to E",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                150,
                3 // Artist ID for Kevin Macleod
        );
        albums.add(missing_Hits_C_to_E);

        Album calming = new Album(
                5,
                "Album",
                "Calming",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                150,
                3 // Artist ID Kevin macleod
        );
        albums.add(calming);

        Album noir = new Album(
                6,
                "Album",
                "Noir",
                null, // Cover will be set dynamically
                new ArrayList<>(), // Empty tracks list initially
                150,
                3 // Artist ID Kevin macleod
        );
        albums.add(noir);

        // Set default cover resource IDs
        albumCoverResourceIds.put(1, R.drawable.test_album_art);
        albumCoverResourceIds.put(2, R.drawable.special_vibe_cover);
        albumCoverResourceIds.put(3, R.drawable.the_fonky_things);
        albumCoverResourceIds.put(4, R.drawable.disquiet_album);
        albumCoverResourceIds.put(5, R.drawable.dream_culture);
        albumCoverResourceIds.put(6, R.drawable.cool_vibes);
    }


    /**
     * Returns a list of all albums
     * @return Local albums
     */
    public List<Album> getAllAlbums() {
        return new ArrayList<>(albums);
    }


    /**
     * Returns an album by its Id
     * @param id Id of the album
     * @return The album
     */
    public Album getAlbumById(int id) {
        return albums.stream()
                .filter(album -> album.getId() == id)
                .findFirst()
                .orElse(null);
    }


    /**
     * Returns a list of albums by an artist
     * @param artistId The id of the artist
     * @return A list of albums
     */
    public List<Album> getAlbumsByArtist(int artistId) {
        List<Album> artistAlbums = new ArrayList<>();
        for (Album album : albums) {
            if (album.getArtistId() == artistId) {
                artistAlbums.add(album);
            }
        }
        return artistAlbums;
    }


    /**
     * Adds an album to the database.
     * Not currently a feature, but would allow users to upload their
     * own personal album to the database.
     * @param album The album being added to the database.
     */
    public void addAlbum(Album album) {
        albums.add(album);

        if (connection == null) {
            return;
        }

        try {
            String query = "INSERT INTO ALBUM (alb_id, alb_type, alb_name, alb_likes, art_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, album.getId());
                ps.setString(2, album.getType());
                ps.setString(3, album.getName());
                ps.setInt(4, album.getLikes());
                ps.setInt(5, album.getArtistId());

                ps.executeUpdate();
                try {
                    connection.commit();
                } catch (SQLException e) {
                    Log.e("BayWaves", "Error committing album addition: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            Log.e("BayWaves", "Database error adding album: " + e.getMessage(), e);
        }
    }


    /**
     * Updates album information in local list and database.
     * Would be used if user/artist wanted to change album info.
     * @param album The album being updated.
     */
    public void updateAlbum(Album album) {
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getId() == album.getId()) {
                albums.set(i, album);
                break;
            }
        }

        if (connection == null) {
            return;
        }

        try {
            String query = "UPDATE ALBUM SET " +
                    "alb_type = ?, alb_name = ?, alb_likes = ?, art_id = ? " +
                    "WHERE alb_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, album.getType());
                ps.setString(2, album.getName());
                ps.setInt(3, album.getLikes());
                ps.setInt(4, album.getArtistId());
                ps.setInt(5, album.getId());

                ps.executeUpdate();
                try {
                    connection.commit();
                } catch (SQLException e) {
                    Log.e("BayWaves", "Error committing album update: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            Log.e("BayWaves", "Database error updating album: " + e.getMessage(), e);
        }
    }


    /**
     * Deletes album from local list and database.
     * Would be used if user or artist wanted to delete an album.
     * @param albumId The Id of the album being deleted
     */
    public void deleteAlbum(int albumId) {
        albums.removeIf(album -> album.getId() == albumId);

        if (connection == null) {
            return;
        }

        try {
            String query = "DELETE FROM ALBUM WHERE alb_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, albumId);

                ps.executeUpdate();
                try {
                    connection.commit();
                } catch (SQLException e) {
                    Log.e("BayWaves", "Error committing album deletion: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            Log.e("BayWaves", "Database error deleting album: " + e.getMessage(), e);
        }
    }

    /**
     * Sets an album cover resource Id
     * @param albumId Id of album
     * @param resourceId Id of cover
     */
    public void setAlbumCoverResourceId(int albumId, int resourceId) {
        albumCoverResourceIds.put(albumId, resourceId);
    }

    /**
     * gets the album cover resource Id from the cache
     * @param albumId The Id of the album
     * @return The resource Id of the album cover
     */
    public int getAlbumCoverResourceId(int albumId) {
        Integer resourceId = albumCoverResourceIds.get(albumId);
        if (resourceId == null) {
            return R.drawable.dafault_album_cover;
        }
        return resourceId;
    }

    /**
     * Adds a track to an album
     * @param albumId The album teh track is being added to
     * @param track The track being added
     */
    public void addTrackToAlbum(int albumId, Track track) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            track.setAlbumId(albumId);
            album.addSong(track);
            //trackRepository.updateTrack(track);
        }
    }

    /**
     * Removes a track from an album
     * @param albumId The Id of the album
     * @param track The track being removed
     */
    public void removeTrackFromAlbum(int albumId, Track track) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            album.removeSong(track);
        }
    }

    /**
     * Returns the artist for an album
     * @param albumId The Id of the album
     * @return The artist for the album
     */
    public Artist getArtistForAlbum(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            return artistRepository.getArtistById(album.getArtistId());
        }
        return null;
    }

    /**
     * Updates an album's number of likes
     * @param albumId The Id of the album
     * @param likesCount The number of likes
     */
    public void updateAlbumLikes(int albumId, int likesCount) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            album.setLikes(likesCount);
            updateAlbum(album);
        }
    }

    /**
     * Increments like count for an album
     * @param albumId The Id of the album
     */
    public void incrementAlbumLikes(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            album.setLikes(album.getLikes() + 1);
            updateAlbum(album);
        }
    }

    /**
     * Decrements like count for an album
     * @param albumId The Id of the album
     */
    public void decrementAlbumLikes(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null && album.getLikes() > 0) {
            album.setLikes(album.getLikes() - 1);
            updateAlbum(album);
        }
    }

    /**
     * Returns a list of tracks on an album
     * @param albumId The Id of the album
     * @return The list of tracks on the album
     */
    public List<Track> getTracksForAlbum(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            return album.getTracks();
        }
        return new ArrayList<>();
    }

    /**
     * Refreshes the tracks on an album
     * @param albumId The Id of the album
     */
    public void refreshAlbumTracks(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            List<Track> updatedTracks = trackRepository.getTracksByAlbum(albumId);
            album.setTracks(updatedTracks);
        }
    }
}