package com.BayWave;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;

public class SearchHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        if (!"GET".equals(exchange.getRequestMethod()))
        {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String SearchString = params.get("searchstring");
        String resultLimitStr = params.get("limit");
        String resultOffsetStr = params.get("offset");

        if (SearchString == null || resultLimitStr == null || resultOffsetStr == null) {
            exchange.sendResponseHeaders(400, -1); // Bad Request
            return;
        }

        SearchString = SearchString.replaceAll("[^a-zA-Z0-9._ -]", "");
        resultLimitStr = resultLimitStr.replaceAll("[^0-9]", "");
        resultOffsetStr = resultOffsetStr.replaceAll("[^0-9]", "");

        int limit = 10;
        int offset = 0;

        //checks if null after sanitization
        try {
            if (resultLimitStr != null) limit = Integer.parseInt(resultLimitStr);
            if (limit > 20) limit = 20;
            if (offset > 100) offset = 100;

            if (resultOffsetStr != null) offset = Integer.parseInt(resultOffsetStr);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        //function to recieve json results of query
        String json = SqlSearcher(SearchString, limit, offset);

        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().close();
    }

    private static String SqlSearcher(String SearchString, int limit, int offset)
    {
        String keyword = "%" + SearchString.toLowerCase() + "%";

        ArrayList<SearchResult> results = new ArrayList<SearchResult>();


        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test"))
        {
            String sql = """
                    SELECT art.art_id, art.art_name,\s
                           alb.alb_id, alb.alb_name,\s
                           trk.trk_id, trk.trk_name, trk.trk_file
                    FROM TRACK trk
                    JOIN ALBUM alb ON trk.alb_id = alb.alb_id
                    JOIN ARTIST art ON alb.art_id = art.art_id
                    WHERE LOWER(art.art_name) LIKE ?
                       OR LOWER(alb.alb_name) LIKE ?
                       OR LOWER(trk.trk_name) LIKE ?
                    ORDER BY trk.trk_likes DESC
                    LIMIT ? OFFSET ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, keyword);
            stmt.setString(2, keyword);
            stmt.setString(3, keyword);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SearchResult r = new SearchResult(
                        rs.getInt("art_id"),
                        rs.getString("art_name"),
                        rs.getInt("alb_id"),
                        rs.getString("alb_name"),
                        rs.getInt("trk_id"),
                        rs.getString("trk_name"),
                        rs.getString("trk_file")
                );
                results.add(r);
            }


        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = gson.toJson(results);

        //here we return formated json for use further on.
        return json;
    }
}
