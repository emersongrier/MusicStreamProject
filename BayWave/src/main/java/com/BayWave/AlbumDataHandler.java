package com.BayWave;

import com.BayWave.Tables.AlbumTable;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;
import static com.BayWave.Util.ServerUtil.getConnection;


class AlbumDataHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        if (params == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        String id = params.get("id");

        if (id == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        // Establish database connection. Replace with your connection details.
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            System.err.println("Unable to establish DB connection: " + e.getMessage());
            return;
        }

        String[] albuminfo;

        try {
            albuminfo = AlbumTable.getAlbum(connection, Integer.parseInt(id));
        } catch (SQLException e) {
            System.err.println("Could not get user info: " + e.getMessage());
            return;
        }

        if (albuminfo == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        AlbumData albumData = new AlbumData(
                Integer.parseInt(albuminfo[0]),
                albuminfo[1],
                albuminfo[2],
                albuminfo[3],
                Integer.parseInt(albuminfo[4]),
                Integer.parseInt(albuminfo[5]),
                Boolean.parseBoolean(albuminfo[6]),
                Integer.parseInt(albuminfo[7]));

        Gson gson = new Gson();
        String json = gson.toJson(albumData);

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