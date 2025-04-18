package com.BayWave.Options;

import com.BayWave.Tables.TrackListenTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class TrackListenOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("TRACK_LISTEN Options:");
        System.out.println("1. Print TRACK_LISTEN table");
        System.out.println("2. Add listen to track for user");
        System.out.println("3. Remove one (1) listen to track for user based on date (don't see any use case)");
        System.out.println("4. Check if user has listened to track");
        System.out.println("5. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String user;
        String track;
        String date;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    TrackListenTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    TrackListenTable.register(connection, user, artist, album, track);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter data (YYYY-MM-DD): ");
                    date = scanner.nextLine();
                    TrackListenTable.deleteOne(connection, user, artist, album, track, date);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Contains: " + TrackListenTable.contains(connection, user, artist, album, track));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
