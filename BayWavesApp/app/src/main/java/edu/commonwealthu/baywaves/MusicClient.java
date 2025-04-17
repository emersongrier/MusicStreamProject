package edu.commonwealthu.baywaves;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MusicClient {

    private final String baseUrl;
    private Context context;

    public MusicClient(Context context) {
        this.baseUrl = "http://140.82.13.163:8080/song";
        this.context = context;
    }

    /**
     * Downloads a song by filename and stores it in a temporary file.
     * Returns a URI string that can be used by ExoPlayer.
     *
     * @param filename Name of the file (e.g., "test.mp3")
     * @return URI string for the downloaded file
     * @throws Exception on download error
     */
    public String downloadSong(String filename) throws Exception {
        String songUrl = baseUrl + "?file=" + filename;
        Log.d("MusicClient", "Downloading song from: " + songUrl);

        URL url = new URL(songUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try {
            // Create a temporary file in the app's cache directory
            File tempFile = File.createTempFile("music_", "_" + filename, context.getCacheDir());

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
     * Gets a direct streaming URL for a song (without downloading)
     *
     * @param filename Name of the file
     * @return URL string for direct streaming
     */
    public String getStreamingUrl(String filename) {
        return baseUrl + "?file=" + filename;
    }
}