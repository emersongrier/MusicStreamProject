package com.BayWave.Options;

import com.BayWave.Tables.AlbumGenreTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class AlbumGenreOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("ALBUM_GENRE Options:");
        System.out.println("1. Print ALBUM_GENRE table");
        System.out.println("2. Add genre to album");
        System.out.println("3. Remove genre from album");
        System.out.println("4. Check if album has genre");
        System.out.println("5. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        do {
            printOptions();
            input = scanner.nextLine();
            String genre;
            switch (input) {
                case "1":
                    AlbumGenreTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    AlbumGenreTable.register(connection, artist, album, genre);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    AlbumGenreTable.delete(connection, artist, album, genre);
                    break;
                case "4":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    System.out.println("Contains: " + AlbumGenreTable.contains(connection, artist, album, genre));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
