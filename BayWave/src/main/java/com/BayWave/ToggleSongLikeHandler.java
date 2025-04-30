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
        String trckid = params.get("trckid");

        if (username == null || password == null || trckid == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        // (Optional) sanitize trckid
        trckid = trckid.replaceAll("[^a-zA-Z0-9._ -]", "");

        Connection connection = null;

        try
        {
            connection = ServerUtil.getConnection();

            // Verify username and password
            boolean valid = UserTable.passwordValid(connection, username, password);
            if (!valid) {
                exchange.sendResponseHeaders(403, -1); // Forbidden
                exchange.getResponseBody().close();
                return;
            }


            String intermediateData[] = TrackTable.getAlbumArtist(connection, Integer.parseInt(trckid));

            //if liked, unlike
            if(LikeTrackTable.contains(connection,username,intermediateData[18],intermediateData[11],intermediateData[1]))
                LikeTrackTable.delete(connection,username,intermediateData[18],intermediateData[11],intermediateData[1]);
            else
                LikeTrackTable.register(connection,username,intermediateData[18],intermediateData[11],intermediateData[1]);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
