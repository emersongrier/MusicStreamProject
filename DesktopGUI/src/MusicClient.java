//

import java.io.InputStream;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URI;
//import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.net.ssl.HttpsURLConnection;


public class MusicClient {

    private final String baseUrl;

    public MusicClient() {
        this.baseUrl = "https://baywave.org:8080/song";
    }

    /**Downloads a song by filename and stores it in a temporary file.
    * Use the path to create media object for javafx*
    * @param trckid trackid
    * @return Path to the temporary downloaded music file
    * @throws Exception on download error
    */
    public Path downloadSong(String trckid) throws Exception
    {
        String songUrl = baseUrl + "?trckid=" + trckid;
        URI uri = new URI(songUrl);
        URL url = uri.toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

            try (InputStream in = conn.getInputStream()) {
                Path tempFile = Files.createTempFile("music", "" + trckid);
                Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return tempFile;
            }
        }



}