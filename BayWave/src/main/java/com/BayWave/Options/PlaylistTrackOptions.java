package com.BayWave.Options;

import com.BayWave.Tables.PlaylistTrackTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class PlaylistTrackOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("PLAYLIST_TRACK Options:");
        System.out.println("1. Print PLAYLIST_TRACK table");
        System.out.println("2. Register track into playlist");
        System.out.println("3. Delete track from playlist");
        System.out.println("4. Change track position (swap)");
        System.out.println("5. Change track position (insert)");
        System.out.println("6. Get track table for playlist ID");
        System.out.println("7. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        String plyName;
        String artist;
        String album;
        String track;
        int newPos;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    PlaylistTrackTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    PlaylistTrackTable.register(connection, name, plyName, artist, album, track);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    PlaylistTrackTable.delete(connection, name, plyName, artist, album, track);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new position (swap): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    PlaylistTrackTable.swapPosition(connection, name, plyName, artist, album, track, newPos);
                    break;
                case "5":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new position (insert): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    PlaylistTrackTable.insertAtPosition(connection, name, plyName, artist, album, track, newPos);
                case "6":
                    System.out.println("Enter playlist ID: ");
                    int plyId = scanner.nextInt();
                    scanner.nextLine();
                    String[] result = PlaylistTrackTable.getTracks(connection, plyId);
                    if (result != null) {
                        for (String s : result) {
                            System.out.print(s + ", ");
                        }
                    }
                    else {
                        System.out.println("Result is null");
                    }
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }

    // TODO: Add ability to add tracks to playlist
}
