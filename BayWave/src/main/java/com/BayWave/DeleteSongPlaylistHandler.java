package com.BayWave;

import com.BayWave.Tables.PlaylistTrackTable;
import com.BayWave.Tables.TrackTable;
import com.BayWave.Tables.UserTable;
import com.BayWave.Util.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DeleteSongPlaylistHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
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
        String playlistname = params.get("playlistname");
        String trackid = params.get("trckid");

        playlistname = playlistname.replaceAll("[^a-zA-Z0-9._ -]", "");

        if (username == null || password == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        Connection connection = null;
        try
        {
            connection = ServerUtil.getConnection();

            boolean valid = UserTable.passwordValid(connection, username, password);
            if (!valid) {
                exchange.sendResponseHeaders(403, -1); // Forbidden
                exchange.getResponseBody().close();
                return;
            }

            String[] data = TrackTable.getAlbumArtist(connection, Integer.parseInt(trackid));
            if (data.length < 19) {
                exchange.sendResponseHeaders(500, -1);
                return;
            }


            PlaylistTrackTable.delete(connection,username,playlistname,data[18],data[11],data[1]);
            exchange.sendResponseHeaders(200, -1);

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
