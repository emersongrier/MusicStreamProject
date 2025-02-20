package com.BayWave.Options;

import com.BayWave.Tables.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class UserOptions {
    public static void printOptions() {
        System.out.println();
        System.out.println("USER_ Options:");
        System.out.println("1. Print USER_ table");
        System.out.println("2. Register user");
        System.out.println("3. Delete user");
        System.out.println("4. Update email");
        System.out.println("5. Update phone number");
        System.out.println("6. Update username");
        System.out.println("7. Delete email");
        System.out.println("8. Delete phone number");
        System.out.println("9. Check password");
        System.out.println("10. Update password");
        System.out.println("11. Manage FRIEND (associative entity)");
        System.out.println("12. Manage FOLLOW_ARTIST (associative entity)");
        System.out.println("13. Return");
        System.out.println();
    }

    public static void options(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String input;
        String username;
        String password;
        do {
            printOptions();
            input = scanner.nextLine();
            switch (input) {
                case "1":
                    User.print(connection);
                    break;
                case "2":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    if (User.usernameExists(connection, username)) {
                        System.out.println("Username already exists");
                        break;
                    }
                    System.out.println("Enter password: ");
                    password = scanner.nextLine();
                    User.register(connection, username, password);
                    break;
                case "3":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    User.delete(connection, username);
                    break;
                case "4":
                    String email;
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter email: ");
                    email = scanner.nextLine();
                    User.addEmail(connection, username, email);
                    break;
                case "5":
                    String phoneNumber;
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter phone number: ");
                    phoneNumber = scanner.nextLine();
                    User.addPhone(connection, username, phoneNumber);
                    break;
                case "6":
                    String newName;
                    System.out.println("Enter current username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter new username: ");
                    newName = scanner.nextLine();
                    User.updateUsername(connection, username, newName);
                    break;
                case "7":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    User.deleteEmail(connection, username);
                    break;
                case "8":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    User.deletePhone(connection, username);
                    break;
                case "9":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter password");
                    password = scanner.nextLine();
                    System.out.println("Password validity: " + User.passwordValid(connection, username, password));
                    break;
                case "10":
                    System.out.println("Enter username: ");
                    username = scanner.nextLine();
                    System.out.println("Enter new password: ");
                    password = scanner.nextLine();
                    User.updatePassword(connection, username, password);
                    break;
                case "11":
                    FriendOptions.options(connection);
                    break;
                case "12":
                    FollowArtistOptions.options(connection);
                    break;
                default:
                    input = "-1";
            }
        } while (!Objects.equals(input, "-1"));
    }
}
