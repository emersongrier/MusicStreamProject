package com.BayWave;

import com.BayWave.Tables.UserTable;
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


class UserGetHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Getting user 1");
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        System.out.println("Getting user 2");
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        if (params == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        System.out.println("Getting user 3");
        String userName = params.get("username");
        String password = params.get("password");

        if (userName == null || password == null) {
            System.out.println("Username: " + userName);
            System.out.println("Password: " + password);
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        System.out.println("Getting user 4");
        // Establish database connection. Replace with your connection details.
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            System.err.println("Unable to establish DB connection: " + e.getMessage());
            return;
        }
        System.out.println("Getting user 5");
        // verify password
        try {
            boolean valid = UserTable.passwordValid(connection, userName, password);
            if (!valid) {
                exchange.sendResponseHeaders(403, -1); // Forbidden
                exchange.getResponseBody().close();
                return;
            }
        } catch (SQLException e) {
            System.out.println("Password invalid for user " + userName + ": " + e.getMessage());
            return;
        }

        System.out.println("Getting user 6");

        String[] userinfo;

        try {
            userinfo = UserTable.getUser(connection, userName);
        } catch (SQLException e) {
            System.err.println("Could not get user info: " + e.getMessage());
            return;
        }

        System.out.println("Getting user 7");

        if (userinfo == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        System.out.println("Getting user 8");

        UserData userData = new UserData(
                Integer.parseInt(userinfo[0]),
                userinfo[1],
                userinfo[2],
                userinfo[3],
                Integer.parseInt(userinfo[4]),
                Integer.parseInt(userinfo[5]));

        Gson gson = new Gson();
        String json = gson.toJson(userData);

        System.out.println("Getting user 9");

        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

        System.out.println("Getting user 10");

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);

        System.out.println("Getting user 11");

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}