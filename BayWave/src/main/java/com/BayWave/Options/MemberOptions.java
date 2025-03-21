package com.BayWave.Options;

import com.BayWave.Tables.MemberTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class MemberOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("MEMBER Options:");
        System.out.println("1. Print MEMBER table");
        System.out.println("2. Register USER_ as MEMBER of ARTIST");
        System.out.println("3. Remove USER_ as MEMBER of ARTIST");
        System.out.println("4. Set USER_ as primary MEMBER of ARTIST");
        System.out.println("5. Check if user is member of artist");
        System.out.println("6. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String user;
            String artist;
            switch (input) {
                case "1":
                    MemberTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    MemberTable.register(connection, user, artist);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    MemberTable.delete(connection, user, artist);
                    break;
                case "4":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    MemberTable.setAsPrimary(connection, user, artist);
                    break;
                case "5":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    System.out.println("Contains: " + MemberTable.contains(connection, user, artist));
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
