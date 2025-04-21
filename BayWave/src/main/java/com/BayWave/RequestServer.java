package com.BayWave;

import com.BayWave.SongHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
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
                    // get the SSLEngine so we can see which ciphers/protocols the server supports
                    SSLContext ctx = getSSLContext();
                    SSLEngine engine = ctx.createSSLEngine();
                    // explicitly enable all the server’s cipher suites and protocols
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // you can still grab the default SSLParameters to pull in any other settings
                    SSLParameters defaultParams = ctx.getDefaultSSLParameters();
                    params.setNeedClientAuth(false);  // you probably don’t want client certs
                    params.setSSLParameters(defaultParams);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up HTTPS", e);
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
