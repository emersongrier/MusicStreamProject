package com.BayWave.Options;

import com.BayWave.Tables.QueueTable;
import com.BayWave.Tables.QueueTrackTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class QueueOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("QUEUE Options:");
        System.out.println("1. Print QUEUE table");
        System.out.println("2. Add track to queue (QUEUE_TRACK)");
        System.out.println("3. Remove track from queue (QUEUE_TRACK)");
        System.out.println("4. Update track position (swap) (QUEUE_TRACK)");
        System.out.println("5. Update track position (insert) (QUEUE_TRACK)");
        System.out.println("6. Print QUEUE_TRACK table");
        System.out.println("7. Print queue for user");
        System.out.println("8. Return");
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
        int newPos;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    QueueTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    QueueTrackTable.register(connection, name, artist, album, track);
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
                    QueueTrackTable.delete(connection, name, artist, album, track);
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
                    QueueTrackTable.swapPosition(connection, artist, album, track, name, newPos);
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
                    QueueTrackTable.insertAtPosition(connection, artist, album, track, name, newPos);
                    break;
                case "6":
                    QueueTrackTable.print(connection);
                    break;
                case "7":
                    System.out.println("Enter username: ");
                    name = scanner.nextLine();
                    ArrayList<String[]> queue = QueueTrackTable.getTableForUser(connection, name);
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
