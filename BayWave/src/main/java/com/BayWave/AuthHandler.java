package com.BayWave;

import com.BayWave.Tables.UserTable;
import com.BayWave.Util.ServerUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // only POST allowed
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // parse JSON body
        Map<String, String> params;
        try (InputStream in = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            params = gson.fromJson(reader, MAP_TYPE);
        } catch (Exception e) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        String username = params != null ? params.get("username") : null;
        String password = params != null ? params.get("password") : null;
        if (username == null || password == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        // check credentials
        try (Connection conn = ServerUtil.getConnection()) {
            boolean valid = UserTable.passwordValid(conn, username, password);
            if (valid) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
