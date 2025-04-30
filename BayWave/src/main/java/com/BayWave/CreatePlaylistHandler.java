package com.BayWave;

import com.BayWave.Tables.PlaylistTable;
import com.BayWave.Tables.UserTable;
import com.BayWave.Util.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class CreatePlaylistHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> params = ServerUtil.parsePostRequest(exchange);
        if (params == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String username     = params.get("username");
        String password     = params.get("password");
        String playlistName = params.get("playlistname");

        if (username == null || password == null || playlistName == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        playlistName = playlistName.replaceAll("[^a-zA-Z0-9._ -]", "");

        try (Connection conn = ServerUtil.getConnection()) {
            if (!UserTable.passwordValid(conn, username, password)) {
                exchange.sendResponseHeaders(401, -1);
                return;
            }

            PlaylistTable.register(conn, username, playlistName);
            boolean created = (PlaylistTable.getPlaylist(conn, username, playlistName) != null);
            if (!created) {
                byte[] conflict = "{\"error\":\"playlist exists\"}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(409, conflict.length);
                exchange.getResponseBody().write(conflict);
            } else {
                byte[] ok = "{\"status\":\"created\"}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type","application/json");
                exchange.sendResponseHeaders(201, ok.length);
                exchange.getResponseBody().write(ok);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
