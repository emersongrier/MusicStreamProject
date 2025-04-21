package com.BayWave;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.Map;

import static com.BayWave.ParseQuery.parseQuery;

public class AmbienceHandler implements HttpHandler
{

    //path to music storage
    private static final String MUSIC_DIR = "/home/developer/Downloads/AmbienceStorage/";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String fileName = params.get("type");

        //sanitizes request
        fileName = fileName.replaceAll("[^a-zA-Z0-9._ -]", "");

        if (fileName == null) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        //creates full path to song file; sends 404 if not exists
        if(fileName.equals("rain")){
            fileName = "rain1.mp3";}
        else if(fileName.equals("fire")){
            fileName = "fire.mp3";}
        else if(fileName.equals("crickets")){
            fileName = "crickets.mp3";}
        else
        {
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
             InputStream is = new BufferedInputStream(new FileInputStream(songFile)))
        {

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

}
