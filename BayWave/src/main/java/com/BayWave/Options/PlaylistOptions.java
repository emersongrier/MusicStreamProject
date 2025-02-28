package com.BayWave.Options;

import com.BayWave.Tables.PlaylistTable;

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
        System.out.println("10. Return");
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
                    PlaylistTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    PlaylistTable.register(connection, name, plyName);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    PlaylistTable.delete(connection, name, plyName);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter playlist description: ");
                    String plyDesc = scanner.nextLine();
                    PlaylistTable.updateDesc(connection, name, plyName, plyDesc);
                    break;
                case "5":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    PlaylistTable.printDesc(connection, name, plyName);
                    break;
                case "6":
                    String newName;
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter new playlist name: ");
                    newName = scanner.nextLine();
                    PlaylistTable.updateName(connection, name, plyName, newName);
                    break;
                case "7":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    plyName = scanner.nextLine();
                    System.out.println("Enter new playlist cover link: ");
                    String cover = scanner.nextLine();
                    PlaylistTable.updateCover(connection, name, plyName, cover);
                    break;
                case "8":
                    PlaylistTrackOptions.options(connection);
                    break;
                case "9":
                    CollaboratePlaylistOptions.options(connection);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
