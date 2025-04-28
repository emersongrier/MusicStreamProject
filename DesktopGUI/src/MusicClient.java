import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.MalformedURLException;
import java.io.OutputStream;
import java.net.URI;
//import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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

    public boolean createAccount(String username, String password) {
        boolean completed = false;
        try {
            URL url = new URL("https://baywave.org:8080/user/post"); // adjust URL
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = buildJson(username, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                completed = true;
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completed;
    }

    private static String buildJson(String username, String password) {
        return "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
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