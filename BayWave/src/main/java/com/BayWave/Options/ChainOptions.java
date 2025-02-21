package com.BayWave.Options;

import com.BayWave.Tables.Chain;
import com.BayWave.Tables.ChainTrack;
import com.BayWave.Tables.Queue;
import com.BayWave.Tables.QueueTrack;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class ChainOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("CHAIN Options:");
        System.out.println("1. Print CHAIN table");
        System.out.println("2. Add chain to playlist");
        System.out.println("3. Remove chain from playlist");
        System.out.println("4. Update track position (swap) (CHAIN_TRACK)");
        System.out.println("5. Update track position (insert) (CHAIN_TRACK)");
        System.out.println("6. Print CHAIN_TRACK table");
        System.out.println("7. Print chains associated with playlist");
        System.out.println("8. Print songs associated with chain");
        System.out.println("9. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        String artist;
        String album;
        String newName;
        String file;
        String track;
        String playlist;
        int newPos;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    Chain.print(connection);
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
                    Chain.register(connection, name, playlist, artist1, album1, track1, artist2, album2, track2);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    QueueTrack.delete(connection, name, artist, album, track);
                    break;
                case "4":
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
                    QueueTrack.swapPosition(connection, artist, album, track, name, newPos);
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
                    System.out.println("Enter new position (insert): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    QueueTrack.insertAtPosition(connection, artist, album, track, name, newPos);
                    break;
                case "6":
                    ChainTrack.print(connection);
                    break;
                case "7":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    ArrayList<String[]> queue = QueueTrack.getTableForUser(connection, name);
                    if (queue != null) {
                        for (String[] row : queue) {
                            for (String string : row) {
                                System.out.print(string + " ");
                            }
                            System.out.println();
                        }
                    }
                    else {
                        System.out.println("Queue does not have any tracks");
                    }
                    break;
                default:
                    System.out.println("DEFAULTING");
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
