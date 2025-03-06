package com.BayWave.Options;

import com.BayWave.Tables.TrackGenreTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class TrackGenreOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("TRACK_GENRE Options:");
        System.out.println("1. Print TRACK_GENRE table");
        System.out.println("2. Add genre to track");
        System.out.println("3. Remove genre from track");
        System.out.println("4. Check if track has genre");
        System.out.println("5. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String track;
        do {
            printOptions();
            input = scanner.nextLine();
            String genre;
            switch (input) {
                case "1":
                    TrackGenreTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    TrackGenreTable.register(connection, artist, album, track, genre);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    TrackGenreTable.delete(connection, artist, album, track, genre);
                    break;
                case "4":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    System.out.println("Contains: " + TrackGenreTable.contains(connection, artist, album, track, genre));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
