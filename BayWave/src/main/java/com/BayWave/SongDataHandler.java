package com.BayWave;

import com.BayWave.Tables.TrackTable;
import com.BayWave.Util.ServerUtil;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

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
        String trckid = params.get("trckid");

        trckid = trckid.replaceAll("[^a-zA-Z0-9._ -]", "");

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
            System.out.println(e);
            throw new RuntimeException(e);
        }

        if (trackinfo == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        TrackData trackData = new TrackData(
                Integer.parseInt(trackinfo[0]),
                trackinfo[1],
                trackinfo[2],
                Integer.parseInt(trackinfo[3]),
                trackinfo[4],
                trackinfo[5],
                Integer.parseInt(trackinfo[6]),
                Integer.parseInt(trackinfo[7]),
                Integer.parseInt(trackinfo[8]));

        Gson gson = new Gson();
        String json = gson.toJson(trackData);

        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
