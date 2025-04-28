package com.BayWave;

import com.BayWave.Tables.TrackTable;
import com.BayWave.Tables.UserTable;
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


public class SongDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        // Parse POST body parameters
        Map<String, String> params = ServerUtil.parsePostRequest(exchange);

        if (params == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        String username = params.get("username");
        String password = params.get("password");
        String trckid = params.get("trckid");

        if (username == null || password == null || trckid == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        // (Optional) sanitize trckid
        trckid = trckid.replaceAll("[^a-zA-Z0-9._ -]", "");

        Connection connection = null;
        try {
            connection = ServerUtil.getConnection();

            // Verify username and password
            boolean valid = UserTable.passwordValid(connection, username, password);
            if (!valid) {
                exchange.sendResponseHeaders(403, -1); // Forbidden
                return;
            }

            // Fetch track info
            String[] trackinfo = TrackTable.getTrack(connection, Integer.parseInt(trckid));

            if (trackinfo == null) {
                exchange.sendResponseHeaders(404, -1); // Not Found
                return;
            }

            // Build response
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
        } catch (SQLException e) {
            System.out.println(e);
            exchange.sendResponseHeaders(500, -1); // Internal Server Error
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
