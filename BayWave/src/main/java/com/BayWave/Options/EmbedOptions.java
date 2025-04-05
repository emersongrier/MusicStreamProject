package com.BayWave.Options;

import com.BayWave.Tables.EmbedTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class EmbedOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("EMBED Options:");
        System.out.println("1. Print EMBED table");
        System.out.println("2. Embed to post");
        System.out.println("3. Remove embedded content from post");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            int post;
            String type;
            int content;
            switch (input) {
                case "1":
                    EmbedTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter post ID: ");
                    post = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter content type (Artist, Album, Track, Playlist (case-sensitive): ");
                    type = scanner.nextLine();
                    System.out.println("Enter content ID: ");
                    content = scanner.nextInt();
                    scanner.nextLine();
                    EmbedTable.register(connection, post, type, content);
                    break;
                case "3":
                    System.out.println("Enter post ID: ");
                    post = scanner.nextInt();
                    scanner.nextLine();
                    EmbedTable.delete(connection, post);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
