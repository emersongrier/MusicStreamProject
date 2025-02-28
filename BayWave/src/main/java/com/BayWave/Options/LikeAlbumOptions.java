package com.BayWave.Options;

import com.BayWave.Tables.LikeAlbumTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class LikeAlbumOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("LIKE_ALBUM Options:");
        System.out.println("1. Print LIKE_ALBUM table");
        System.out.println("2. Add album to user's likes");
        System.out.println("3. Remove album from user's likes");
        System.out.println("4. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String user;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    LikeAlbumTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    LikeAlbumTable.register(connection, user, artist, album);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    LikeAlbumTable.delete(connection, user, artist, album);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
