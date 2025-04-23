package com.BayWave;

import com.BayWave.Tables.UserTable;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;
import static com.BayWave.Util.ServerUtil.getConnection;


class UserPostHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // TODO: FIX

        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
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

        if (userName != null && password != null) {
            boolean result = false;
            try {
                result = UserTable.register(connection, userName, password);
            }
            catch (SQLException e) {
                System.err.println("Error posting user \"" + userName + "\": " + e.getMessage());
            }
            if (result) {
                exchange.sendResponseHeaders(201, -1);
            }
            else {
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }
}