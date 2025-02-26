package com.BayWave.Options;

import com.BayWave.Tables.TrackGenreTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class TrackGenreOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("TRACK_GENRE Options:");
        System.out.println("1. Print TRACK_GENRE table");
        System.out.println("2. Add genre to track");
        System.out.println("3. Remove genre from track");
        System.out.println("4. Return");
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
                    TrackGenreTable.print(connection);
                    break;
                case "2":

                    break;
                case "3":

                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
