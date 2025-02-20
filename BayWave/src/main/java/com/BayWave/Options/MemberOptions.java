package com.BayWave.Options;

import com.BayWave.Tables.Member;

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
        System.out.println("4. Return");
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
                    Member.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    Member.register(connection, user, artist);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter artist name: ");
                    artist = scanner.nextLine();
                    Member.delete(connection, user, artist);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
