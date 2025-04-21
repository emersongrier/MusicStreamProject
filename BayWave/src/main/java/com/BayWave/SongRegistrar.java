package com.BayWave;


import com.BayWave.Tables.AlbumTable;
import com.BayWave.Tables.TrackGenreTable;
import com.BayWave.Tables.TrackTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SongRegistrar {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java SongRegistrar <file_listing_path> <common_directory_path>");
            System.exit(1);
        }

        String listingFilePath = args[0];
        String commonDirPath = args[1];

        // Establish database connection. Replace with your connection details.
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            System.err.println("Unable to establish DB connection: " + e.getMessage());
            System.exit(1);
        }

        String currentGenre = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(listingFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip blank lines
                }

                // Check if line is a genre header (ends with a colon)
                if (line.endsWith(":")) {
                    // Example header: "./A/Contemporary:" -> we want "Contemporary"
                    int lastSlash = line.lastIndexOf('/');
                    if (lastSlash != -1) {
                        currentGenre = line.substring(lastSlash + 1, line.length() - 1);
                    } else {
                        currentGenre = line.substring(0, line.length() - 1);
                    }
                    System.out.println("Genre changed to: " + currentGenre);
                } else {
                    // Remove any trailing commas (if any) from song name
                    String songName = line.replaceAll(",$", "");

                    // Check if the song file exists in the common directory
                    File songFile = new File(commonDirPath, songName);
                    if (!songFile.exists()) {
                        System.out.println("Song file does not exist: " + songFile.getAbsolutePath());
                        continue;
                    }

                    // Use default values for artist and type; adjust these as needed.
                    String artist = "Kevin MacLeod";
                    String album = songName; // Album name is same as song name
                    String track = songName; // Track name is same as song name
                    String type = "single";     // For example

                    try {
                        // Register the album (each song gets its own album)
                        AlbumTable.register(connection, artist, album, type);

                        // Register the track with the album of its same name
                        TrackTable.register(connection, artist, album, track);

                        // Register the genre to the track (if a genre has been set)
                        if (currentGenre != null) {
                            TrackGenreTable.register(connection, artist, album, track, currentGenre);
                        } else {
                            System.out.println("No genre found for track: " + track);
                        }

                        // Update the track to include the file path (absolute path)
                        TrackTable.updateFile(connection, artist, album, track, songFile.getAbsolutePath());

                    } catch (SQLException e) {
                        System.err.println("Error processing song \"" + songName + "\": " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file listing: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    /**
     * Replace this method with your actual database connection logic.
     */
    private static Connection getConnection() throws SQLException {
        // Example using DriverManager.
        // Update the URL, username, and password according to your DB setup.
        String url = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        return DriverManager.getConnection(url);
    }
}
