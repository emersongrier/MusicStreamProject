package com.BayWave;

import com.BayWave.Tables.UserTable;
import com.BayWave.Util.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
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

            //if liked, unlike
            //if not liked, like


        }
    }
}
