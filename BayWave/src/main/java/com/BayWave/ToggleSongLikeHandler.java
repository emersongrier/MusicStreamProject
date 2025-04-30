package com.BayWave;

import com.BayWave.Tables.LikeTrackTable;
import com.BayWave.Tables.TrackTable;
import com.BayWave.Tables.UserTable;
import com.BayWave.Util.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ToggleSongLikeHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> params = ServerUtil.parsePostRequest(exchange);
        if (params == null ||
                params.get("username") == null ||
                params.get("password") == null ||
                params.get("trckid") == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String username = params.get("username");
        String password = params.get("password");
        String trckid = params.get("trckid").replaceAll("[^a-zA-Z0-9._ -]", "");

        Connection connection = null;
        try {
            connection = ServerUtil.getConnection();
            if (!UserTable.passwordValid(connection, username, password)) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            String[] data = TrackTable.getAlbumArtist(connection, Integer.parseInt(trckid));
            if (data.length < 19) {
                exchange.sendResponseHeaders(500, -1);
                return;
            }

            if (LikeTrackTable.contains(connection, username, data[18], data[11], data[1])) {
                LikeTrackTable.delete(connection, username, data[18], data[11], data[1]);
            } else {
                LikeTrackTable.register(connection, username, data[18], data[11], data[1]);
            }

            exchange.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            if (connection != null) try { connection.close(); } catch (SQLException ignored) {}
            exchange.getResponseBody().close();
        }
    }
}
