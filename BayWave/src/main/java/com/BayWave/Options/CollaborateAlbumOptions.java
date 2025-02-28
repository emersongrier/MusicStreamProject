package com.BayWave.Options;

import com.BayWave.Tables.CollaborateAlbumTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class CollaborateAlbumOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("COLLABORATE_ALBUM Options:");
        System.out.println("1. Print COLLABORATE_ALBUM table");
        System.out.println("2. Set artist as collaborator for album");
        System.out.println("3. Remove artist as collaborator for playlist");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String collab;
            String primary;
            String album;
            switch (input) {
                case "1":
                    CollaborateAlbumTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter name of collaborating artist: ");
                    collab = scanner.nextLine();
                    System.out.println("Enter name of primary album artist: ");
                    primary = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    CollaborateAlbumTable.register(connection, collab, primary, album);
                    break;
                case "3":
                    System.out.println("Enter name of collaborating artist: ");
                    collab = scanner.nextLine();
                    System.out.println("Enter name of primary album artist: ");
                    primary = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    CollaborateAlbumTable.delete(connection, collab, primary, album);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
