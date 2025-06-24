package edu.commonwealthu.baywaves;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

public class MusicClient {

    private final String baseUrl;
    private Context context;
    private String username; // For authentication
    private String password; // For authentication

    public MusicClient(Context context) {
        this.baseUrl = "https://baywave.org:8080";
        this.context = context;
    }

    /**
     * Sets login credentials
     * @param username of user
     * @param password of user
     */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets a direct streaming URL for a song (without downloading)
     *
     * @param trackId ID of the track
     * @return URL string for direct streaming
     */
    public String getStreamingUrl(String trackId) {
        return baseUrl + "/song?trckid=" + trackId;
    }


    /**
     * Downloads a song by track ID and stores it in a temporary file.
     * Returns a URI string that can be used by ExoPlayer.
     *
     * @param trackId ID of the track
     * @return URI string for the downloaded file
     * @throws Exception on download error
     */
    public String downloadSong(String trackId) throws Exception {
        String songUrl = baseUrl + "/song?trckid=" + trackId;
        Log.d("MusicClient", "Downloading song from: " + songUrl);

        URL url = new URI(songUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try {
            // Create a temporary file in the app's cache directory
            File tempFile = File.createTempFile("music_", "_" + trackId, context.getCacheDir());

            // Download the file
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Return the URI as a string that can be used by ExoPlayer
            return Uri.fromFile(tempFile).toString();

        } finally {
            conn.disconnect();
        }
    }


    /**
     * Gets metadata for a song using credentials
     *
     * @param trackId ID of the track
     * @return JSON string with song metadata
     */
    public String downloadSongMetadata(String trackId) {
        if (username == null || password == null) {
            Log.w("MusicClient", "No credentials set for metadata request");
            return null;
        }

        try {
            String songUrl = baseUrl + "/song/metadata";
            URL url = new URI(songUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Tell server it's JSON
            conn.setRequestProperty("Content-Type", "application/json");

            // Build JSON
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("trckid", trackId);
            jsonMap.put("username", username);
            jsonMap.put("password", password);
            Gson gson = new Gson();
            String json = gson.toJson(jsonMap);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Success
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            } else {
                // Failure (403, 404, etc) → just return null
                Log.e("MusicClient", "Failed to get metadata, response code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e("MusicClient", "Error downloading song metadata", e);
            return null;
        }
    }

    /**
     * Search the database for tracks
     */
    public String searchDb(String searchString, int limit, int offset) {
        try {
            String encodedSearch = URLEncoder.encode(searchString, "UTF-8");
            String songUrl = baseUrl + "/search?searchstring=" + encodedSearch
                    + "&limit=" + URLEncoder.encode("" + limit, "UTF-8")
                    + "&offset=" + URLEncoder.encode("" + offset, "UTF-8");

            URL url = new URI(songUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return reader.lines().collect(Collectors.joining());
            }
        } catch (Exception e) {
            Log.e("MusicClient", "Error searching database", e);
            return null;
        }
    }

    /**
     * Toggle like status for a song
     */
    public boolean toggleSongLike(String trackId) {
        if (username == null || password == null) {
            Log.w("MusicClient", "No credentials set for like toggle");
            return false;
        }

        try {
            String toggleUrl = baseUrl + "/song/like";
            URL url = new URI(toggleUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Build JSON payload
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("username", username);
            jsonMap.put("password", password);
            jsonMap.put("trckid", trackId);
            String json = new Gson().toJson(jsonMap);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            conn.disconnect();
            // 200 OK means toggle succeeded
            return code == 200;
        } catch (Exception e) {
            Log.e("MusicClient", "Error toggling song like", e);
            return false;
        }
    }
}