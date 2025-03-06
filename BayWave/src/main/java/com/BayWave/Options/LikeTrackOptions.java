package com.BayWave.Options;

import com.BayWave.Tables.LikeTrackTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class LikeTrackOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("LIKE_TRACK Options:");
        System.out.println("1. Print LIKE_TRACK table");
        System.out.println("2. Add track to user's likes");
        System.out.println("3. Remove track from user's likes");
        System.out.println("4. Check if user has track in their likes");
        System.out.println("5. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String track;
        String user;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    LikeTrackTable.print(connection);
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
                    LikeTrackTable.register(connection, user, artist, album, track);
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
                    LikeTrackTable.delete(connection, user, artist, album, track);
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
                    System.out.println("Contains: " + LikeTrackTable.contains(connection, user, artist, album, track));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
