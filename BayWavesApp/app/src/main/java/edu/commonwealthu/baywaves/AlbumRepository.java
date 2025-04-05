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

public class AlbumRepository {
    private static AlbumRepository instance;
    private List<Album> albums;
    private Connection connection;
    private final Map<Integer, Integer> albumCoverResourceIds = new HashMap<>(); // Store resource IDs instead of ImageViews
    private TrackRepository trackRepository;
    private ArtistRepository artistRepository;

    private AlbumRepository() {
        albums = new ArrayList<>();
        trackRepository = TrackRepository.getInstance();
        artistRepository = ArtistRepository.getInstance();

        try {
            // Establish database connection
            connection = DatabaseConnection.getConnection();

            // Fetch albums from database
            loadAlbumsFromDatabase();
        } catch (Exception e) {
            Log.e("BayWaves", "Database connection failed: " + e.getMessage(), e);

            // Fallback to default albums if database connection fails
            addDefaultAlbums();
        }
    }

    public static synchronized AlbumRepository getInstance() {
        if (instance == null) {
            instance = new AlbumRepository();
        }
        return instance;
    }

    private void loadAlbumsFromDatabase() throws SQLException {
        // Implement method to fetch albums from the database
        if (connection == null) {
            Log.e("BayWaves", "Database connection is null");
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

                // Get tracks for this album
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

        // If no albums found, add default albums
        if (albums.isEmpty()) {
            addDefaultAlbums();
        }
    }

    private void addDefaultAlbums() {
        Log.d("BayWaves", "Adding default albums");

        // Add default albums with empty track lists first
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

        // Now try to populate tracks if available
        try {
            for (Album album : albums) {
                List<Track> tracks = trackRepository.getTracksByArtist(album.getArtistId());
                if (tracks != null && !tracks.isEmpty()) {
                    album.setTracks(tracks);
                }
            }
        } catch (Exception e) {
            Log.e("BayWaves", "Error loading tracks for default albums: " + e.getMessage(), e);
            // Continue even if track loading fails
        }

        // Set default cover resource IDs
        albumCoverResourceIds.put(1, R.drawable.test_album_art);
        albumCoverResourceIds.put(2, R.drawable.special_vibe_cover);
        albumCoverResourceIds.put(3, R.drawable.the_fonky_things);
    }

    public List<Album> getAllAlbums() {
        return new ArrayList<>(albums);
    }

    public Album getAlbumById(int id) {
        return albums.stream()
                .filter(album -> album.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Album> getAlbumsByArtist(int artistId) {
        List<Album> artistAlbums = new ArrayList<>();
        for (Album album : albums) {
            if (album.getArtistId() == artistId) {
                artistAlbums.add(album);
            }
        }
        return artistAlbums;
    }

    public void addAlbum(Album album) {
        albums.add(album);

        if (connection == null) {
            return; // Skip database operations if no connection
        }

        try {
            // Insert album into database
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

    public void updateAlbum(Album album) {
        // Update in local list
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getId() == album.getId()) {
                albums.set(i, album);
                break;
            }
        }

        if (connection == null) {
            return; // Skip database operations if no connection
        }

        try {
            // Update in database
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

    public void deleteAlbum(int albumId) {
        // Remove from local list
        albums.removeIf(album -> album.getId() == albumId);

        if (connection == null) {
            return; // Skip database operations if no connection
        }

        try {
            // Delete from database
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

    // Method to set album cover resource ID
    public void setAlbumCoverResourceId(int albumId, int resourceId) {
        // Update the cover resource ID in cache
        albumCoverResourceIds.put(albumId, resourceId);
    }

    // Method to get album cover resource ID from cache
    public int getAlbumCoverResourceId(int albumId) {
        Integer resourceId = albumCoverResourceIds.get(albumId);
        if (resourceId == null) {
            // Return a default resource ID if not found
            return R.drawable.default_playlist;
        }
        return resourceId;
    }

    // Add a track to an album
    public void addTrackToAlbum(int albumId, Track track) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            // Set the album ID in the track
            track.setAlbumId(albumId);

            // Add track to album's track list
            album.addSong(track);

            // Update track in repository
            trackRepository.updateTrack(track);
        }
    }

    // Remove a track from an album
    public void removeTrackFromAlbum(int albumId, Track track) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            // Remove track from album's track list
            album.removeSong(track);
        }
    }

    // Get artist information for an album
    public Artist getArtistForAlbum(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            return artistRepository.getArtistById(album.getArtistId());
        }
        return null;
    }

    // Update likes count for an album
    public void updateAlbumLikes(int albumId, int likesCount) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            album.setLikes(likesCount);
            updateAlbum(album);
        }
    }

    // Increment likes count for an album
    public void incrementAlbumLikes(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            album.setLikes(album.getLikes() + 1);
            updateAlbum(album);
        }
    }

    // Decrement likes count for an album
    public void decrementAlbumLikes(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null && album.getLikes() > 0) {
            album.setLikes(album.getLikes() - 1);
            updateAlbum(album);
        }
    }

    // Get all tracks for a specific album
    public List<Track> getTracksForAlbum(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            return album.getTracks();
        }
        return new ArrayList<>();
    }

    // Refresh tracks for an album (useful after database updates)
    public void refreshAlbumTracks(int albumId) {
        Album album = getAlbumById(albumId);
        if (album != null) {
            List<Track> updatedTracks = trackRepository.getTracksByAlbum(albumId);
            album.setTracks(updatedTracks);
        }
    }
}