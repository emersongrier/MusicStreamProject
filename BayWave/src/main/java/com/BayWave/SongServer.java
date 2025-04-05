package com.BayWave;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public class SongServer {

    private static final String MUSIC_DIR = "/home/developer/Downloads/MusicStorage/"; // <-- Change this

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/song", new SongHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    static class SongHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);
            String fileName = params.get("file");
	    fileName = fileName.replaceAll("[^a-zA-Z0-9._ -]", "");

            if (fileName == null) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            File songFile = new File(MUSIC_DIR + fileName);
            if (!songFile.exists()) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "audio/mpeg");
            exchange.sendResponseHeaders(200, songFile.length());

            try (OutputStream os = exchange.getResponseBody();
                InputStream is = new BufferedInputStream(new FileInputStream(songFile))) {
    
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                     os.write(buffer, 0, bytesRead);
                }
                os.flush();
                System.out.println("Finished streaming: " + songFile.getName());

             } catch (IOException e) {
                    e.printStackTrace();
             }
             }

        private Map<String, String> parseQuery(String query) {
            if (query == null) return Map.of();
            return java.util.Arrays.stream(query.split("&"))
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(
                            p -> URLDecoder.decode(p[0], java.nio.charset.StandardCharsets.UTF_8),
                            p -> URLDecoder.decode(p[1], java.nio.charset.StandardCharsets.UTF_8)
                    ));
        }
    }
}
