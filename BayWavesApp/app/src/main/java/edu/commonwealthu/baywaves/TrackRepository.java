package edu.commonwealthu.baywaves;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackRepository {
    private static final String TAG = "TrackRepository";
    private static TrackRepository instance;
    private final Map<Integer, Boolean> likedTracksCache;
    private MusicClient musicClient;
    private Context context;
    private String name;
    private String Id;
    private String artist;

    private String username = "20jakeleonardo@gmail.com";
    private String password = "bayWaves88$";
    private boolean isLoggedIn = false;

    private TrackRepository() {
        likedTracksCache = new HashMap<>();
    }

    public static synchronized TrackRepository getInstance() {
        if (instance == null) {
            instance = new TrackRepository();
        }
        return instance;
    }

    /**
     * Sets context for the music client
     * @param context of the music client
     */
    public void setContext(Context context) {
        this.context = context;
        if (this.context != null) {
            musicClient = new MusicClient(context);
        }
    }

    // Method to set login credentials
    public boolean login(String username, String password) {
        this.username = username;
        this.password = password;
        if (musicClient != null) {
            musicClient.setCredentials(username, password);
            // In a real app, you'd verify credentials with the server here
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Add a method to get the streaming URI for a track
    public String getStreamingUri(Track track) {
        if (musicClient == null) {
            Log.e(TAG, "MusicClient not initialized. Context not set.");
            return track.getFilePath(); // Return the local path as fallback
        }

        try {
            // Make sure we have a valid track ID
            if (track.getId() <= 0) {
                Log.e(TAG, "Invalid track ID: " + track.getId() + " for " + track.getName());
                return track.getFilePath();
            }

            Log.d(TAG, "Getting streaming URI for track: " + track.getId() + " - " + track.getName());

            // For server tracks, use the track ID directly
            if (track.getId() > 0) {
                String trackId = String.valueOf(track.getId());
                Log.d(TAG, "Using track ID for streaming: " + trackId);

                try {
                    // Try to get streaming URL using track ID
                    String streamingUrl = musicClient.getStreamingUrl(trackId);
                    Log.d(TAG, "Got streaming URL: " + streamingUrl);
                    return streamingUrl;
                } catch (Exception e) {
                    Log.e(TAG, "Error getting streaming URL: " + e.getMessage(), e);

                    // Try download as fallback
                    try {
                        String downloadPath = musicClient.downloadSong(trackId);
                        Log.d(TAG, "Downloaded to: " + downloadPath);
                        return downloadPath;
                    } catch (Exception e2) {
                        Log.e(TAG, "Download also failed: " + e2.getMessage(), e2);
                    }
                }
            }

            // If we couldn't get a streaming URI, return the file path
            return track.getFilePath();
        } catch (Exception e) {
            Log.e(TAG, "Error in getStreamingUri: " + e.getMessage(), e);
            return track.getFilePath();
        }
    }

    // Helper to extract track ID from file path
    private String extractTrackId(String filePath) {
        if (filePath.endsWith(".mp3") && !filePath.contains("/")) {
            // Look up the track by filename and return its ID
            List<Track> tracks = getLocalFallbackTracks();
            for (Track track : tracks) {
                if (track.getFilePath().equals(filePath)) {
                    return String.valueOf(track.getId());
                }
            }
        }

        // Your existing code for path extraction
        if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }

    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();

        // Try to get tracks from server
        if (musicClient != null) {
            try {
                // Use search with empty string to get all tracks
                String jsonResponse = musicClient.searchDb("", 100, 0);
                if (jsonResponse != null) {
                    tracks = parseTracksFromJson(jsonResponse);
                    if (!tracks.isEmpty()) {
                        return tracks;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching tracks from server", e);
            }
        }

        // If server fails, return local fallback tracks
        return getLocalFallbackTracks();
        //return tracks;
    }

    // Parse tracks from JSON response
    public List<Track> parseTracksFromJson(String json) {
        List<Track> tracks = new ArrayList<>();
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject trackJson = jsonArray.get(i).getAsJsonObject();

                    // Parse track with all available fields
                    int id = trackJson.has("trk_id") ? trackJson.get("trk_id").getAsInt() : 0;
                    String name = trackJson.has("trk_name") ? trackJson.get("trk_name").getAsString() : "Unknown";
                    String file = trackJson.has("trk_file") ? trackJson.get("trk_file").getAsString() : "";
                    int position = trackJson.has("trk_pos") ? trackJson.get("trk_pos").getAsInt() : 0;
                    String lyrics = trackJson.has("trk_lyrics") ? trackJson.get("trk_lyrics").getAsString() : "";
                    int length = trackJson.has("trk_len") ? trackJson.get("trk_len").getAsInt() : 0;
                    int streams = trackJson.has("trk_strms") ? trackJson.get("trk_strms").getAsInt() : 0;
                    int likes = trackJson.has("trk_likes") ? trackJson.get("trk_likes").getAsInt() : 0;
                    int albumId = trackJson.has("alb_id") ? trackJson.get("alb_id").getAsInt() : 0;
                    int artistId = trackJson.has("art_id") ? trackJson.get("art_id").getAsInt() : 0;

                    Track track = new Track(id, name, file, position, lyrics, length, streams, likes, albumId, artistId);
                    tracks.add(track);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return tracks;
    }

    public Track getTrackById(int id) {
        Log.d(TAG, "Getting track by ID: " + id);

        // Try to get from server first
        if (musicClient != null) {
            try {
                Log.d(TAG, "Attempting to get track " + id + " from server");
                String metadata = musicClient.downloadSongMetadata(String.valueOf(id));

                if (metadata != null && !metadata.isEmpty()) {
                    Log.d(TAG, "Server returned metadata for track " + id);
                    List<Track> tracks = parseTracksFromJson("[" + metadata + "]");
                    if (!tracks.isEmpty()) {
                        Log.d(TAG, "Successfully parsed track from server: " + tracks.get(0).getName());
                        return tracks.get(0);
                    }
                } else {
                    Log.d(TAG, "Server returned no metadata for track " + id);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching track " + id + " from server: " + e.getMessage(), e);
            }
        }

        // Check local tracks as last resort
        Log.d(TAG, "Falling back to local tracks for ID: " + id);
        for (Track track : getLocalFallbackTracks()) {
            if (track.getId() == id) {
                Log.d(TAG, "Found track in local fallback: " + track.getName());
                return track;
            }
        }

        Log.e(TAG, "Track ID " + id + " not found in server or locally");
        return null;
    }

    // Add this new method to ensure a track has all necessary info
    public Track completeTrackInfo(Track track) {
        if (track == null) {
            Log.e(TAG, "Cannot complete info for null track");
            return null;
        }

        Log.d(TAG, "Completing track info for: " + track.getId() + " - " + track.getName());

        // If this track has a file path but no ID, try to find the ID
        if (track.getId() <= 0 && track.getFilePath() != null && !track.getFilePath().isEmpty()) {
            Log.d(TAG, "Track has path but no ID, searching for ID: " + track.getFilePath());
            // Try to identify by file name
            List<Track> allTracks = getAllTracks();
            for (Track t : allTracks) {
                if (track.getFilePath().equals(t.getFilePath()) ||
                        track.getFilePath().contains(t.getFilePath()) ||
                        t.getFilePath().contains(track.getFilePath())) {

                    Log.d(TAG, "Found matching track by file path: " + t.getId());
                    return t; // Return the complete track
                }
            }
        }

        // If track has ID but missing other info, try to get the complete track
        if (track.getId() > 0) {
            Track completeTrack = getTrackById(track.getId());
            if (completeTrack != null) {
                Log.d(TAG, "Found complete track info for ID: " + track.getId());
                return completeTrack;
            }
        }

        // If we couldn't complete the info, return the original track
        return track;
    }

    public void updateTrack(Track track) {
        // Try to update on server if it's a like toggle
        if (musicClient != null) {
            // Check if this is a like/unlike operation
            boolean isLiking = track.isLocalLikedState();
            boolean wasLiked = isTrackLiked(track.getId());

            if (isLiking != wasLiked) {
                // It's a like toggle operation
                boolean success = musicClient.toggleSongLike(String.valueOf(track.getId()));
                if (success) {
                    // Update local cache to match
                    setTrackLiked(track.getId(), isLiking);
                    return;
                }
            }
        }

        // No fallback needed here since we're not using local DB anymore
        Log.d(TAG, "Track update attempted but server call failed: " + track.getId());
    }

    // Get all tracks by a specific artist
    public List<Track> getTracksByArtist(int artistId) {
        List<Track> artistTracks = new ArrayList<>();

        // Try to filter from all tracks
        List<Track> allTracks = getAllTracks();
        for (Track track : allTracks) {
            if (track.getArtistId() == artistId) {
                artistTracks.add(track);
            }
        }

        return artistTracks;
    }

    // Get tracks by album ID
    public List<Track> getTracksByAlbum(int albumId) {
        List<Track> albumTracks = new ArrayList<>();

        // Filter from all tracks
        List<Track> allTracks = getAllTracks();
        for (Track track : allTracks) {
            if (track.getAlbumId() == albumId) {
                albumTracks.add(track);
            }
        }

        return albumTracks;
    }

    public int getAlbumIdForTrack(int trackId) {
        Track track = getTrackById(trackId);
        if (track != null) {
            return track.getAlbumId();
        }
        return -1; // Return a default/error value
    }

    public void setTrackLiked(int trackId, boolean isLiked) {
        likedTracksCache.put(trackId, isLiked);

        // If we have a music client, also update on server
        if (musicClient != null) {
            boolean currentServerLikeState = false;

            // Try to get current state from server
            try {
                String metadata = musicClient.downloadSongMetadata(String.valueOf(trackId));
                if (metadata != null) {
                    JsonObject trackJson = JsonParser.parseString(metadata).getAsJsonObject();
                    if (trackJson.has("liked")) {
                        currentServerLikeState = trackJson.get("liked").getAsBoolean();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting like state from server", e);
            }

            // If state is different, toggle it
            if (currentServerLikeState != isLiked) {
                musicClient.toggleSongLike(String.valueOf(trackId));
            }
        }
    }

    public boolean isTrackLiked(int trackId) {
        if (musicClient != null) {
            try {
                String metadata = musicClient.downloadSongMetadata(String.valueOf(trackId));
                if (metadata != null) {
                    JsonObject trackJson = JsonParser.parseString(metadata).getAsJsonObject();
                    if (trackJson.has("liked")) {
                        return trackJson.get("liked").getAsBoolean();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting like state from server", e);
            }
        }

        return likedTracksCache.getOrDefault(trackId, false);
    }

    public boolean isServerConnected() {
        if (musicClient != null) {
            try {
                String response = musicClient.searchDb("test", 1, 0);
                return response != null;
            } catch (Exception e) {
                Log.e(TAG, "Server not available", e);
                return false;
            }
        }
        return false;
    }

    public String getConnectionErrorMessage() {
        if (musicClient != null) {
            try {
                String response = musicClient.searchDb("test", 1, 0);
                if (response != null) {
                    return "Server Connected Successfully!";
                } else {
                    return "Server Connection Failed: No response";
                }
            } catch (Exception e) {
                return "Server Connection Failed: " + e.getMessage();
            }
        }
        return "MusicClient not initialized";
    }

    // Local fallback tracks as a last resort
    protected List<Track> getLocalFallbackTracks() {
        List<Track> fallbackTracks = new ArrayList<>();


        // Track from Yarin Primak in a different album
        fallbackTracks.add(new Track(
                12,
                "Disquiet",
                "Disquiet.mp3",
                1,
                "Lyrics for Disquiet",
                0,
                500,
                0,
                4,  // Album ID for "Disquiet"
                3   // Artist ID for Kevin MacLeod
        ));

        fallbackTracks.add(new Track(
                13,
                "Dream Culture",
                "Dream Culture.mp3",
                2,
                "Sample lyrics for Dream Culture",
                0,
                0,
                0,
                5,  // Album ID for "Dream Culture"
                3   // Artist ID for Kevin Macleod
        ));

        // Additional track from Aves in the same album
        fallbackTracks.add(new Track(
                799,
                "Cool Vibes",
                "Cool Vibes.mp3",
                3,
                "Lyrics for Cool Vibes",
                0,
                750,
                0,
                6,
                3   // Same Artist ID for Kevin Macleod
        ));

        return fallbackTracks;
    }


}