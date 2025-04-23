package com.BayWave;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.BayWave.Tables.PlaylistTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;


class PlaylistHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String userName = params.get("user");
        String playlistName = params.get("playlist");

        // Establish database connection. Replace with your connection details.
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            System.err.println("Unable to establish DB connection: " + e.getMessage());
            System.exit(1);
        }

        if (playlistName != null && userName != null) {
            String[] playlistResult = null;
            try {
                playlistResult = PlaylistTable.getPlaylist(connection, userName, playlistName);
            }
            catch (SQLException e) {
                System.err.println("Error processing playlist \"" + playlistName + "\": " + e.getMessage());
            }
            if (playlistResult == null) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            for (String cell : playlistResult) {
                byte[] responseBytes = cell.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Replace this method with your actual database connection logic.
     */
    private static Connection getConnection() throws SQLException {
        // Example using DriverManager.
        // Update the URL, username, and password according to your DB setup.
        String url = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        return DriverManager.getConnection(url);
    }
}