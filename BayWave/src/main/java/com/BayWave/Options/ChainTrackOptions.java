package com.BayWave.Options;

import com.BayWave.Tables.ChainTable;
import com.BayWave.Tables.ChainTrackTable;
import com.BayWave.Tables.QueueTrackTable;

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
        System.out.println("6. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        String artist;
        String album;
        int chainId;
        String track;
        String playlist;
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

                    break;
                case "4":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Chain ID: " + ChainTable.getChainIdWithPlaylistAndTrack(connection, name, playlist, artist, album, track));
                    break;
                case "5":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new position (swap): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    QueueTrackTable.swapPosition(connection, artist, album, track, name, newPos);
                    break;
                default:
                    System.out.println("DEFAULTING");
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
