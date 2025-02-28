package com.BayWave.Options;

import com.BayWave.Tables.AlbumTable;

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
        System.out.println("8. Manage ALBUM_GENRE (associative entity)");
        System.out.println("9. Manage LIKE_ALBUM (associative entity)");
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
                    AlbumTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter album type (Single, EP, LP): ");
                    type = scanner.nextLine();
                    AlbumTable.register(connection, artist, album, type);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album name: ");
                    String newName = scanner.nextLine();
                    AlbumTable.updateName(connection, artist, album, newName);
                    break;
                case "4":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album type (Single, EP, LP): ");
                    type = scanner.nextLine();
                    AlbumTable.updateType(connection, artist, album, type);
                    break;
                case "5":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter new album cover link: ");
                    String cover = scanner.nextLine();
                    AlbumTable.updateCover(connection, artist, album, cover);
                    break;
                case "6":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    AlbumTable.delete(connection, artist, album);
                    break;
                case "7":
                    CollaborateAlbumOptions.options(connection);
                    break;
                case "8":
                    AlbumGenreOptions.options(connection);
                    break;
                case "9":
                    LikeAlbumOptions.options(connection);
                    break;
                case "10":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    ArrayList<String[]> tracks = AlbumTable.getTracks(connection, artist, album);
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
