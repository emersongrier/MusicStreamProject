package com.BayWave;

import com.BayWave.SongHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

import com.BayWave.SongHandler;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class RequestServer {

    /**
     * Creates an Http Server for communication
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8443), 0);

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

            server.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }


        //creates contexts that will then be handled by class handler designed for that context

        //creates simple song requests
        server.createContext("/song", new SongHandler());
        server.createContext("/song/metadata", new SongDataHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/ambience", new AmbienceHandler());
        server.createContext("/playlist", new PlaylistHandler());

        //test to push to server



        server.setExecutor(null);
        server.start();
        System.out.println("Server started on https://localhost:8080");
    }
}
