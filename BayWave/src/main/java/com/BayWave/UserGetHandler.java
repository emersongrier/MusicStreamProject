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
        System.out.println("Getting user");
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            System.exit(1);
        }
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        if (params == null) {
            exchange.sendResponseHeaders(400, -1);
            System.exit(1);
        }
        String userName = params.get("username");
        String password = params.get("password");

        // Establish database connection. Replace with your connection details.
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            System.err.println("Unable to establish DB connection: " + e.getMessage());
            System.exit(1);
        }

        // verify password
        boolean passwordValid = false;
        try {
            passwordValid = UserTable.passwordValid(connection, userName, password);
        } catch (SQLException e) {
            System.err.println("Password invalid for user " + userName + ": " + e.getMessage());
            System.exit(1);
        }

        if (passwordValid) {

            String[] userinfo = null;

            try {
                connection = getConnection();
                userinfo = UserTable.getUser(connection, userName);
            } catch (SQLException e) {
                System.err.println("Could not get user info: " + e.getMessage());
                System.exit(1);
            }

            if (userinfo == null) {
                exchange.sendResponseHeaders(404, -1);
                System.exit(1);
            }

            UserData userData = new UserData(
                    Integer.parseInt(userinfo[0]),
                    userinfo[1],
                    userinfo[2],
                    userinfo[3],
                    Integer.parseInt(userinfo[4]),
                    Integer.parseInt(userinfo[5]));

            Gson gson = new Gson();
            String json = gson.toJson(userData);

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
        else {
            System.err.println("Password invalid for user " + userName);
            exchange.sendResponseHeaders(400, -1);
        }
    }
}