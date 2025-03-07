package com.BayWave.Options;

import com.BayWave.Tables.ChainTrackTable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class ChainTrackOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("CHAIN_TRACK Options:");
        System.out.println("1. Print CHAIN_TRACK table");
        System.out.println("2. Add track to chain");
        System.out.println("3. Remove track from chain");
        System.out.println("4. Update track position (swap) (CHAIN_TRACK)");
        System.out.println("5. Update track position (insert) (CHAIN_TRACK)");
        System.out.println("6. Check if chain has track");
        System.out.println("7. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        int chainId;
        String track;
        int newPos;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    ChainTrackTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    ChainTrackTable.addTrack(connection, chainId, artist, album, track);
                    break;
                case "3":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    ChainTrackTable.removeTrack(connection, chainId, artist, album, track);
                    break;
                case "4":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new position (swap) (CHAIN_TRACK): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    ChainTrackTable.swapPosition(connection, chainId, artist, album, track, newPos);
                    break;
                case "5":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new position (insert) (CHAIN_TRACK): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    ChainTrackTable.insertAtPosition(connection, chainId, artist, album, track, newPos);
                    break;
                case "6":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Contains: " + ChainTrackTable.contains(connection, chainId, artist, album, track));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
