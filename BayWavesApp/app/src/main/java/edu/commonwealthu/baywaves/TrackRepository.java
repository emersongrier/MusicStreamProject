package edu.commonwealthu.baywaves;

import android.content.Context;

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
    private HomeFragment homeFragment;
    private Context context;
    private String name;
    private String Id;
    private String artist;

    private String username = "20jakeleonardo@gmail.com";
    private String password = "bayWaves88$";
    private boolean isLoggedIn = false;

    /**
     * Keeps track of tracks that are liked
     */
    private TrackRepository() {
        likedTracksCache = new HashMap<>();
    }


    /**
     * Returns current instance of what track
     * @return Instance of the track repository
     */
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

    /**
     * Method to set login credentials (not currently used)
     *
     * @param username Username
     * @param password Password
     * @return authorization of credentials
     */
    public boolean login(String username, String password) {
        this.username = username;
        this.password = password;
        if (musicClient != null) {
            musicClient.setCredentials(username, password);
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * Helper method to check login status
     * @return True is logged in
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }


    /**
     * Helper method to extract a track ID by its filepath
     * @param filePath Filepath of the current song
     * @return The track ID
     */
    private String extractTrackId(String filePath) {
        if (filePath.endsWith(".mp3") && !filePath.contains("/")) {
            List<Track> tracks = getLocalFallbackTracks();
            for (Track track : tracks) {
                if (track.getFilePath().equals(filePath)) {
                    return String.valueOf(track.getId());
                }
            }
        }

        if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }

    /**
     * Method that returns a list of loaded tracks
     * If the server connection cannot be found, it returns the default fallBackTracks
     *
     * @return A list of tracks (usually fallback since no track if automatically loaded when app is started)
     */
    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();

        if (musicClient != null) {
            String jsonResponse = musicClient.searchDb("", 100, 0);
            if (jsonResponse != null) {
                tracks = parseTracksFromJson(jsonResponse);
                if (!tracks.isEmpty()) {
                    return tracks;
                }
            }

        }

        return getLocalFallbackTracks();
    }

    /**
     * Parses the tracks from a json response to set all available metadata.
     *
     * @param json The song name to be parsed
     * @return A list of songs parsed from the json response
     */
    public List<Track> parseTracksFromJson(String json) {
        List<Track> tracks = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(json);
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject trackJson = jsonArray.get(i).getAsJsonObject();
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

        return tracks;
    }


    /**
     * Get all tracks by a specific artist (find better use)
     * @param artistId The artist's ID
     * @return A list of tracks by that artist
     */
    public List<Track> getTracksByArtist(int artistId) {
        List<Track> artistTracks = new ArrayList<>();

        List<Track> allTracks = getAllTracks();
        for (Track track : allTracks) {
            if (track.getArtistId() == artistId) {
                artistTracks.add(track);
            }
        }

        return artistTracks;
    }

    /**
     * Gets all the tracks from an album (find better use)
     * @param albumId The ID of the album
     * @return A list of tracks from the album
     */
    public List<Track> getTracksByAlbum(int albumId) {
        List<Track> albumTracks = new ArrayList<>();

        List<Track> allTracks = getAllTracks();
        for (Track track : allTracks) {
            if (track.getAlbumId() == albumId) {
                albumTracks.add(track);
            }
        }

        return albumTracks;
    }



    /**
     * Sets a track to liked into the likedTracksCache
     * @param trackId The trackID to know what song is liked
     * @param isLiked Status of track's liked state
     */
    public void setTrackLiked(int trackId, boolean isLiked) {
        likedTracksCache.put(trackId, isLiked);

        if(isLiked) {
            musicClient.toggleSongLike(String.valueOf(trackId));
        }
    }


    /**
     * Checks if a track is liked by checking the local cache first,
     * then falling back to the track's like count if not in cache
     *
     * @param trackId The ID of the track to check
     * @return true if the track is liked, false otherwise
     */
    public boolean isTrackLiked(int trackId) {
        if (likedTracksCache.containsKey(trackId)) {
            return likedTracksCache.get(trackId);
        }

        List<Track> allTracks = getAllTracks();
        for (Track track : allTracks) {
            if (track.getId() == trackId) {
                return track.getLikes() > 0;
            }
        }

        return false;
    }


    /**
     * Helper method to check if app is connected to the server
     * @return True is connected, false if not connected
     */
    public boolean isServerConnected() {
        if (musicClient != null) {
            String response = musicClient.searchDb("", 1, 0);
            return response != null;
        }
        return false;
    }

    /**
     * Displays message based on server connection status
     * @return String saying if server is connected or not
     */
    public String getConnectionErrorMessage() {
        if (musicClient != null) {
            String response = musicClient.searchDb("", 1, 0);
            if (response != null) {
                return "Server Connected Successfully!";
            } else {
                return "Server Connection Failed: No response";
            }

        }
        return "MusicClient not initialized";
    }

    /**
     * Hardcoded list of songs if music client is not detected when app is loaded.
     * Metadata is filled manually since most songs don't have an album cover and most
     * artists don't have a set ID
     *
     * @return list of fallback tracks
     */
    protected List<Track> getLocalFallbackTracks() {
        List<Track> fallbackTracks = new ArrayList<>();

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