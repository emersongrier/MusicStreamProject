package com.BayWave.Options;

import com.BayWave.Tables.LikePostTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class LikePostOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("LIKE_POST Options:");
        System.out.println("1. Print LIKE_POST table");
        System.out.println("2. Add post to user's likes");
        System.out.println("3. Remove post from user's likes");
        System.out.println("4. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String user;
        int id;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    LikePostTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post ID: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    LikePostTable.register(connection, user, id);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post ID: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    LikePostTable.delete(connection, user, id);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
