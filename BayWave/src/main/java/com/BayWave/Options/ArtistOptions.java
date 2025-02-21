package com.BayWave.Options;

import com.BayWave.Tables.ArtistTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class ArtistOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("ARTIST Options:");
        System.out.println("1. Print ARTIST table");
        System.out.println("2. Register artist");
        System.out.println("3. Delete artist");
        System.out.println("4. Update bio");
        System.out.println("5. Print artist bio");
        System.out.println("6. Update artist name");
        System.out.println("7. Manage MEMBER (associative entity)");
        System.out.println("8. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String name;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    ArtistTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter artist name: ");
                    name = scanner.nextLine();
                    ArtistTable.register(connection, name);
                    break;
                case "3":
                    System.out.println("Enter artist name: ");
                    name = scanner.nextLine();
                    ArtistTable.delete(connection, name);
                    break;
                case "4":
                    String bio;
                    System.out.println("Enter artist name: ");
                    name = scanner.nextLine();
                    System.out.println("Enter bio: ");
                    bio = scanner.nextLine();
                    ArtistTable.updateBio(connection, name, bio);
                    break;
                case "5":
                    System.out.println("Enter artist name: ");
                    name = scanner.nextLine();
                    ArtistTable.printBio(connection, name);
                    break;
                case "6":
                    String newName;
                    System.out.println("Enter current artist name: ");
                    name = scanner.nextLine();
                    System.out.println("Enter new artist name: ");
                    newName = scanner.nextLine();
                    ArtistTable.updateName(connection, name, newName);
                    break;
                case "7":
                    MemberOptions.options(connection);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
