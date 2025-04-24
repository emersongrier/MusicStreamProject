package com.BayWave;

import com.BayWave.Tables.TrackTable;
import com.BayWave.Util.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;


class SongHandler implements HttpHandler
{

    //path to music storage
    private static final String MUSIC_DIR = "/home/developer/Downloads/MusicStorage/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String trckid = params.get("trckid");

        //sanitizes request
        trckid = trckid.replaceAll("[^a-zA-Z0-9._ -]", "");
        System.out.println("sanitized" + trckid);

        if (trckid == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        Connection connection;
        String[] trackinfo;

        try {
            connection = ServerUtil.getConnection();
            trackinfo = TrackTable.getTrack(connection,Integer.parseInt(trckid));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        File songFile;

        if (trackinfo == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        System.out.println("path" + trackinfo[2]);

        songFile = new File(trackinfo[2]);

        if (!songFile.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", "audio/mpeg");
        exchange.sendResponseHeaders(200, songFile.length());

        try (OutputStream os = exchange.getResponseBody();
             InputStream is = new BufferedInputStream(new FileInputStream(songFile)))
        {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            System.out.println("Finished streaming: " + songFile.getName());

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

}

