package com.BayWave.Options;

import com.BayWave.Tables.PostTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class PostOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("POST Options:");
        System.out.println("1. Print POST table");
        System.out.println("2. Register post");
        System.out.println("3. Reply to post");
        System.out.println("4. Delete post");
        System.out.println("5. Edit post");
        System.out.println("6. Manage LIKE_POST (associative entity)");
        System.out.println("7. Manage MEDIA");
        System.out.println("8. Manage EMBED (associative entity)");
        System.out.println("9. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String user;
        String text;
        int id;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    PostTable.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post text: ");
                    text = scanner.nextLine();
                    PostTable.register(connection, user, text);
                    break;
                case "3":
                    System.out.println("Enter post ID: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter username: ");
                    user = scanner.nextLine();
                    System.out.println("Enter post text: ");
                    text = scanner.nextLine();
                    PostTable.reply(connection, user, text, id);
                    break;
                case "4":
                    System.out.println("Enter post ID: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    PostTable.delete(connection, id);
                    break;
                case "5":
                    System.out.println("Enter post ID: ");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter new post text: ");
                    text = scanner.nextLine();
                    PostTable.edit(connection, id, text);
                    break;
                case "6":
                    LikePostOptions.options(connection);
                    break;
                case "7":
                    MediaOptions.options(connection);
                    break;
                    /*
                case "7":
                    MemberOptions.options(connection);
                    break;*/
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
