package com.BayWave;

import com.BayWave.Options.*;
import com.BayWave.Tables.Artist;
import com.BayWave.Util.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Admin {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;")) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("Connected to BayWave database as Admin");
            }
            Scanner scanner = new Scanner(System.in);
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
                        QueueOptions.options(connection);
                        break;
                    case "7":
                        ChainOptions.options(connection);
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
