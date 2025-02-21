package com.BayWave.Options;

import com.BayWave.Tables.Album;
import com.BayWave.Tables.Playlist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class PlaylistOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("PLAYLIST Options:");
        System.out.println("1. Print PLAYLIST table");
        System.out.println("2. Register playlist");
        System.out.println("3. Delete playlist");
        System.out.println("4. Update playlist description");
        System.out.println("5. Print playlist description");
        System.out.println("6. Update playlist name");
        System.out.println("7. Update playlist cover");
        System.out.println("8. Manage PLAYLIST_TRACK (associative entity)");
        System.out.println("9. Manage COLLABORATE_PLAYLIST (associative entity)");
        System.out.println("10. Print associated CHAIN_ entities");
        System.out.println("11. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        String plyName;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    Playlist.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    Playlist.register(connection, name, plyName);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    Playlist.delete(connection, name, plyName);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter playlist description: ");
                    String plyDesc = scanner.nextLine();
                    Playlist.updateDesc(connection, name, plyName, plyDesc);
                    break;
                case "5":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    Playlist.printDesc(connection, name, plyName);
                    break;
                case "6":
                    String newName;
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter new playlist name: ");
                    newName = scanner.nextLine();
                    Playlist.updateName(connection, name, plyName, newName);
                    break;
                case "7":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter new playlist cover link: ");
                    String cover = scanner.nextLine();
                    Playlist.updateCover(connection, name, plyName, cover);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
