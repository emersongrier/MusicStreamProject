package com.BayWave;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class RequestServer {

    /**
     * Creates an Http Server for communication
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        //creates contexts that will then be handled by class handler designed for that context

        //creates simple song requests
        server.createContext("/song", new SongHandler());
        server.createContext("/song/metadata", new SongDataHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/ambience", new AmbienceHandler());



        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
