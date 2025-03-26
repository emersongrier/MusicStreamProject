package com.BayWave.Options;

import com.BayWave.Tables.LikePostTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class LikePostOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("POST Options:");
        System.out.println("1. Add post to user's likes");
        System.out.println("2. Remove post from user's likes");
        System.out.println("3. Return");
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
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    scanner.nextLine();
                    LikePostTable.register(connection, user, id);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    scanner.nextLine();
                    // LikePostTable.delete(connection, user, id);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
