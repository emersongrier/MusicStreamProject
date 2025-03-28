package com.BayWave.Options;

import com.BayWave.Tables.MediaTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class MediaOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("MEDIA Options:");
        System.out.println("1. Print MEDIA table");
        System.out.println("2. Add media to post");
        System.out.println("3. Remove media from post");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            int post;
            switch (input) {
                case "1":
                    MediaTable.print(connection);
                    break;
                case "2":
                    String mediaFile;
                    System.out.println("Enter post ID: ");
                    post = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter media filepath: ");
                    mediaFile = scanner.nextLine();
                    MediaTable.register(connection, post, mediaFile);
                    break;
                case "3":
                    int mediaId;
                    System.out.println("Enter media ID: ");
                    mediaId = scanner.nextInt();
                    scanner.nextLine();
                    MediaTable.delete(connection, mediaId);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
