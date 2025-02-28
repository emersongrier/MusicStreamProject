package com.BayWave.Options;

import com.BayWave.Tables.FollowPlaylistTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class FollowPlaylistOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("FOLLOW_PLAYLIST Options:");
        System.out.println("1. Print FOLLOW_PLAYLIST table");
        System.out.println("2. Set user as follower of playlist");
        System.out.println("3. Remove user as follower of playlist");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String follower;
            String owner;
            String playlist;
            switch (input) {
                case "1":
                    FollowPlaylistTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter playlist owner: ");
                    owner = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    System.out.println("Enter playlist follower: ");
                    follower = scanner.nextLine();
                    FollowPlaylistTable.register(connection, owner, playlist, follower);
                    break;
                case "3":
                    System.out.println("Enter playlist owner: ");
                    owner = scanner.nextLine();
                    System.out.println("Enter playlist name: ");
                    playlist = scanner.nextLine();
                    System.out.println("Enter playlist follower: ");
                    follower = scanner.nextLine();
                    FollowPlaylistTable.delete(connection, owner, playlist, follower);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
