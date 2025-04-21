//

import java.io.InputStream;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URI;
//import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


public class MusicClient {

    private final String baseUrl;

    public MusicClient() {
        this.baseUrl = "http://140.82.13.163:8080/song";
    }

    /**Downloads a song by filename and stores it in a temporary file.
    * Use the path to create media object for javafx*
    * @param filename Name of the file (e.g., "test.mp3")
    * @return Path to the temporary downloaded file
    * @throws Exception on download error
    */
    public Path downloadSong(String filename) throws Exception {
        String songUrl = baseUrl + "?file=" + filename;
        URI uri = new URI(songUrl);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

            try (InputStream in = conn.getInputStream()) {
                Path tempFile = Files.createTempFile("music", "" + filename);
                Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return tempFile;
            }
        }

}