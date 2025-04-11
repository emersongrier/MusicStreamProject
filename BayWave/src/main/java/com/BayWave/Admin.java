package com.BayWave;

import com.BayWave.Options.*;
import com.BayWave.Util.*;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Admin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Embedded");
        System.out.println("2. Server (non-functional)");
        System.out.println("Enter connection mode: ");
        String connectionString;
        String mode = scanner.nextLine();
        if (Objects.equals(mode, "1")) {
            connectionString = "jdbc:h2:~/test;AUTOCOMMIT=OFF;";
        }
        else {
            connectionString = "jdbc:h2:tcp://140.82.13.163:9092/./test;AUTOCOMMIT=OFF;";
        }
        // try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;")) { // :~/test
        try (Connection connection = DriverManager.getConnection(connectionString)) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("Connected to BayWave database as Admin");
            }
            String input;
            do {
                AdminUtil.printOptions();
                input = scanner.nextLine();
                switch (input) {
                    case "1": // USER_
                        UserOptions.options(connection);
                        break;
                    case "2":
                        ArtistOptions.options(connection);
                        break;
                    case "3":
                        AlbumOptions.options(connection);
                        break;
                    case "4":
                        TrackOptions.options(connection);
                        break;
                    case "5":
                        PlaylistOptions.options(connection);
                        break;
                    case "6":
                        QueueTrackOptions.options(connection);
                        break;
                    case "7":
                        ChainOptions.options(connection);
                        break;
                    case "8":
                        GenreOptions.options(connection);
                        break;
                    case "9":
                        PostOptions.options(connection);
                        break;
                    default:
                        input = "-1";
                        System.out.println("Goodbye!");
                }
            } while (!Objects.equals(input, "-1"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
