package com.BayWave.Options;

import com.BayWave.Tables.TrackTable;
import com.BayWave.Util.TableUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class TrackOptions {
    private static void printOptions() {
        System.out.println();
        System.out.println("TRACK Options:");
        System.out.println("1. Print TRACK table");
        System.out.println("2. Register track into album");
        System.out.println("3. Delete track from album");
        System.out.println("4. Update track name");
        System.out.println("5. Update track audio file");
        System.out.println("6. Print track lyrics");
        System.out.println("7. Update track lyrics (.txt file)");
        System.out.println("8. Update track position (swap)");
        System.out.println("9. Update track position (insert)");
        System.out.println("10. Manage LIKE_TRACK (associative entity)");
        System.out.println("11. Manage TRACK_LISTEN (associative entity)");
        System.out.println("12. Get track");
        System.out.println("13. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String artist;
        String album;
        String track;
        String newName;
        String file;
        int newPos;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    TrackTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    TrackTable.register(connection, artist, album, track);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    TrackTable.delete(connection, artist, album, track);
                    break;
                case "4":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter current track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new track name: ");
                    newName = scanner.nextLine();
                    TrackTable.updateName(connection, artist, album, track, newName);
                    break;
                case "5":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter track filepath: ");
                    file = scanner.nextLine();
                    TrackTable.updateFile(connection, artist, album, track, file);
                    break;
                case "6":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    TrackTable.printLyrics(connection, artist, album, track);
                    break;
                case "7":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter lyrics filepath: ");
                    file = scanner.nextLine();
                    TrackTable.updateLyrics(connection, artist, album, track, file);
                    break;
                case "8":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new track position (swap): ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    TrackTable.swapPosition(connection, artist, album, track, newPos);
                    break;
                case "9":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    System.out.println("Enter new track position: ");
                    newPos = scanner.nextInt();
                    scanner.nextLine();
                    TrackTable.insertAtPosition(connection, artist, album, track, newPos);
                    break;
                case "10":
                    LikeTrackOptions.options(connection);
                    break;
                case "11":
                    TrackListenOptions.options(connection);
                    break;
                case "12":
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Enter album name: ");
                    album = scanner.nextLine();
                    System.out.println("Enter track name: ");
                    track = scanner.nextLine();
                    String[] table = TrackTable.getTrack(connection, artist, album, track);
                    if (table != null) {
                        TableUtil.printStringTable(table);
                    }
                    else {
                        System.err.println("Track not found");
                    }
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
