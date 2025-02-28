package com.BayWave.Options;

import com.BayWave.Tables.CollaboratePlaylistTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class CollaboratePlaylistOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("COLLABORATE_PLAYLIST Options:");
        System.out.println("1. Print COLLABORATE_PLAYLIST table");
        System.out.println("2. Set user as collaborator for playlist");
        System.out.println("3. Remove user as collaborator for playlist");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String collab;
            String owner;
            String playlist;
            switch (input) {
                case "1":
                    CollaboratePlaylistTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username of collaborator: ");
                    collab = scanner.nextLine();
                    System.out.println("Enter username of playlist owner: ");
                    owner = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    CollaboratePlaylistTable.register(connection, collab, owner, playlist);
                    break;
                case "3":
                    System.out.println("Enter username of collaborator: ");
                    collab = scanner.nextLine();
                    System.out.println("Enter username of playlist owner: ");
                    owner = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    CollaboratePlaylistTable.delete(connection, collab, owner, playlist);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
