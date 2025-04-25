package com.BayWave.Util;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ServerUtil {
    /**
     * Replace this method with your actual database connection logic.
     */
    public static Connection getConnection() throws SQLException {
        // Example using DriverManager.
        // Update the URL, username, and password according to your DB setup.
        DriverManager.registerDriver(new org.h2.Driver());
        String url = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        return DriverManager.getConnection(url);
    }

    public static Map<String, String> parsePostRequest(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        if (requestBody == null) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
        }
    }
}
