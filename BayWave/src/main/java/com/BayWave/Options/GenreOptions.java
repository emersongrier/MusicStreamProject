package com.BayWave.Options;

import com.BayWave.Tables.GenreTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class GenreOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("GENRE Options:");
        System.out.println("1. Print GENRE table");
        System.out.println("2. Register genre");
        System.out.println("3. Delete genre");
        System.out.println("4. Update genre name");
        System.out.println("5. Manage TRACK_GENRE");
        System.out.println("6. Return");
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            printOptions();
            input = scanner.nextLine();
            String genre;
            switch (input) {
                case "1":
                    GenreTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    GenreTable.register(connection, genre);
                    break;
                case "3":
                    System.out.println("Enter genre name: ");
                    genre = scanner.nextLine();
                    GenreTable.delete(connection, genre);
                    break;
                case "4":
                    String newGenre;
                    System.out.println("Enter current genre name: ");
                    genre = scanner.nextLine();
                    System.out.println("Enter new genre name: ");
                    newGenre = scanner.nextLine();
                    GenreTable.updateName(connection, genre, newGenre);
                    break;
                case "5":
                    TrackGenreOptions.options(connection);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
