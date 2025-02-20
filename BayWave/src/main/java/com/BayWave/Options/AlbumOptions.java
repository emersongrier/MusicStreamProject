package com.BayWave.Options;

import com.BayWave.Tables.Album;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class AlbumOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("ALBUM Options:");
        System.out.println("1. Print ALBUM table");
        System.out.println("2. Register album under artist");
        System.out.println("3. Change album name");
        System.out.println("4. Change album type");
        System.out.println("5. Change album cover");
        System.out.println("6. Delete album");
        System.out.println("7. Manage COLLABORATE_ALBUM (associative entity)");
        System.out.println("8. Manage ALBUM_TRACK (associative entity)");
        System.out.println("9. Manage ALBUM_GENRE (associative entity)");
        System.out.println("10. Get associated tracks");
        System.out.println("11. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String type;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    Album.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter album type (Single, EP, LP): ");
                    type = scanner.nextLine();
                    Album.register(connection, artist, album, type);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album name: ");
                    String newName = scanner.nextLine();
                    Album.updateName(connection, artist, album, newName);
                    break;
                case "4":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album type (Single, EP, LP): ");
                    type = scanner.nextLine();
                    Album.updateType(connection, artist, album, type);
                    break;
                case "5":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album cover link: ");
                    String cover = scanner.nextLine();
                    Album.updateCover(connection, artist, album, cover);
                    break;
                case "6":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    Album.delete(connection, artist, album);
                    break;
                case "10":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    ArrayList<String[]> tracks = Album.getTracks(connection, artist, album);
                    if (tracks != null) {
                        for (String[] track : tracks) {
                            for (String string : track) {
                                System.out.print(string + " ");
                            }
                            System.out.println();
                        }
                    }
                    else {
                        System.out.println("Album does not have any tracks");
                    }
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
