package com.BayWave.Options;

import com.BayWave.Tables.ChainTable;
import com.BayWave.Tables.ChainTrackTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class ChainOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("CHAIN Options:");
        System.out.println("1. Print CHAIN table");
        System.out.println("2. Add chain to playlist");
        System.out.println("3. Remove chain from playlist");
        System.out.println("4. Get chain ID");
        System.out.println("5. Print chains associated with playlist");
        System.out.println("6. Print tracks associated with chain");
        System.out.println("7. Manage CHAIN_TRACK");
        System.out.println("8. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        String artist;
        String album;
        String track;
        String playlist;
        String[] chainList;
        int chainId;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    ChainTable.print(connection);
                    break;
                case "2":
                    String artist1;
                    String artist2;
                    String album1;
                    String album2;
                    String track1;
                    String track2;
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    System.out.println("Enter artist 1: ");
                    artist1 = scanner.nextLine();
                    System.out.println("Enter album 1: ");
                    album1 = scanner.nextLine();
                    System.out.println("Enter track 1: ");
                    track1 = scanner.nextLine();
                    System.out.println("Enter artist 2: ");
                    artist2 = scanner.nextLine();
                    System.out.println("Enter album 2: ");
                    album2 = scanner.nextLine();
                    System.out.println("Enter track 2: ");
                    track2 = scanner.nextLine();
                    ChainTable.register(connection, name, playlist, artist1, album1, track1, artist2, album2, track2);
                    break;
                case "3":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    ChainTable.delete(connection, chainId);
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
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    chainList = ChainTable.getTableForPlaylist(connection, name, playlist);
                    if (chainList == null) {
                        System.out.println("Chain list is null");
                    }
                    else {
                        for (String s : chainList) {
                            System.out.println(s);
                        }
                    }
                    break;
                case "6":
                    System.out.println("Enter chain ID: ");
                    chainId = scanner.nextInt();
                    scanner.nextLine();
                    chainList = ChainTrackTable.getTableForChain(connection, chainId);
                    if (chainList == null) {
                        System.out.println("Chain list is null");
                    }
                    else {
                        for (String s : chainList) {
                            System.out.println(s);
                        }
                    }
                    break;
                case "7":
                    ChainTrackOptions.options(connection);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
