import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.MalformedURLException;
import java.net.URI;
//import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

public class MusicClient {

    private final String baseUrl;

    public MusicClient() {
        this.baseUrl = "https://baywave.org:8080";
    }

    /**Downloads a song by filename and stores it in a temporary file.
     * Use the path to create media object for javafx*
     * @param trckid trackid
     * @return Path to the temporary downloaded music file
     * @throws Exception on download error
     */
    public Path downloadSong(String trckid) throws Exception
    {
        String songUrl = baseUrl + "/song?trckid=" + trckid;
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

    public String downloadSongData(String trckid) throws Exception
    {

        String songUrl = baseUrl + "/song/metadata?trckid=" + URLEncoder.encode(trckid, "UTF-8");
        URL url = new URI(songUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public String searchDb(String searchstring, int limit, int offset) throws Exception
    {

        String songUrl = baseUrl + "/search?searchstring=" + URLEncoder.encode(searchstring, "UTF-8") + "&limit=" + URLEncoder.encode("" + limit, "UTF-8") + "&offset=" + URLEncoder.encode("" + offset, "UTF-8");
        URL url = new URI(songUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }


     //for testing
     /*public static void main(String[] args)
     {
     MusicClient mc = new MusicClient();
     try {
     System.out.println(mc.searchDb("Jazz",10,0));
     } catch (Exception e) {
     throw new RuntimeException(e);
     }
     }*/

}