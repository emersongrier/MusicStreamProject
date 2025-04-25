package com.BayWave.Util;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    private static String decode(String encoded) {
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseRequestBody(String requestBody) {
        Map<String, String> params = new HashMap<>();
        if (requestBody != null && !requestBody.isEmpty()) {
            Arrays.stream(requestBody.split("&")).forEach(param -> {
                String[] parts = param.split("=", 2); // Split into at most 2 parts
                String key = decode(parts[0]);
                String value = parts.length > 1 ? decode(parts[1]) : ""; // Handle cases with no value
                params.put(key, value);
            });
        }
        return params;
    }

    // TODO: THESE PARSE FUNCTIONS NEED TESTING

    /**
     * Parses POST request, returning it as a String
     */
    public static Map<String, String> parsePostRequest(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        if (requestBody == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return parseRequestBody(body.toString());
        }
    }
}
