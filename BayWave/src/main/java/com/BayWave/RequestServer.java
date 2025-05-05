package com.BayWave;

import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.*;
import java.util.concurrent.Executors;

public class RequestServer {

    /**
     * Creates an Http Server for communication
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8080), 0);

        // Load SSL certificate
        String passwordEnv = System.getenv("KEYSTORE_PASSWORD");
        if (passwordEnv == null) {
            throw new IllegalStateException("Environment variable KEYSTORE_PASSWORD not set");
        }
        char[] password = passwordEnv.toCharArray();


        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("/home/developer/keystore.p12");
            ks.load(fis, password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            // after sslContext.init(…)…
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters params) {
                    // use the SSLContext’s defaults for ciphers & protocols
                    params.setSSLParameters(getSSLContext().getDefaultSSLParameters());
                }
            });
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Failed to set up HTTPS", e);
        }

        server.createContext("/song", new SongHandler());
        server.createContext("/song/metadata", new SongDataHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/ambience", new AmbienceHandler());
        server.createContext("/playlist", new PlaylistGetHandler());
        server.createContext("/user/post", new UserPostHandler());
        server.createContext("/user/metadata", new UserGetHandler());
        server.createContext("/song/like", new ToggleSongLikeHandler());
        server.createContext("/playlist/create", new CreatePlaylistHandler());
        server.createContext("/playlist/addsong",new AddSongPlaylistHandler());
        server.createContext("/playlist/deletesong", new DeleteSongPlaylistHandler());
        server.createContext("/playlist/songs", new PlaylistSongsHandler());
        server.createContext("/playlist/chains", new PlaylistChainsHandler());
        server.createContext("/artist/metadata", new ArtistDataHandler());
        server.createContext("/album/metadata", new AlbumDataHandler());
        server.createContext("/chain/songs", new ChainSongsHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("[DEBUG] HTTPS listening on " + server.getAddress());
        System.out.println("Server started on https://localhost:8080");
    }
}
