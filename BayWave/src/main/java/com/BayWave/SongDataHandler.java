package com.BayWave;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;


public class SongDataHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String fileName = params.get("file");

        fileName = fileName.replaceAll("[^a-zA-Z0-9._ -]", "");

        if (fileName == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String json = "";
        /**
         * Set up metadata json here
         */

        //get song metadata here


        exchange.getResponseHeaders().set("Content-Type", "application/json");

        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSongMetaData(String SongName)
    {
        

        return null;
    }
}
