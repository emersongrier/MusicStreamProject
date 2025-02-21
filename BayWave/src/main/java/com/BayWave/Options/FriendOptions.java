package com.BayWave.Options;

import com.BayWave.Tables.FriendTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class FriendOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("FRIEND Options:");
        System.out.println("1. Print FRIEND table");
        System.out.println("2. Register friends");
        System.out.println("3. Delete friends");
        System.out.println("4. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String friend1;
        String friend2;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    FriendTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter friend 1 name: ");
                    friend1 = scanner.nextLine();
                    System.out.println("Enter friend 2 name: ");
                    friend2 = scanner.nextLine();
                    FriendTable.register(connection, friend1, friend2);
                    break;
                case "3":
                    System.out.println("Enter friend 1 name: ");
                    friend1 = scanner.nextLine();
                    System.out.println("Enter friend 2 name: ");
                    friend2 = scanner.nextLine();
                    FriendTable.delete(connection, friend1, friend2);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
