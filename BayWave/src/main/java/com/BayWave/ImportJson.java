package com.BayWave;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ImportJson {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF");) { // :~/test
            System.out.println("connection.isValid(0): " + connection.isValid(0));
            if (connection.isValid(0)) {
                System.out.println("Connected to BayWave database");
            }
            Scanner scanner = new Scanner(System.in);
            System.out.println("Warning: this can create duplicate entries currently");
            System.out.println("Enter .json file from resources (<name>.json): ");
            String path = scanner.nextLine();
            File file = new File(System.getProperty("user.dir") + "/BayWave/src/main/resources/" + path);
            JsonFactory factory = new JsonFactory();
            JsonParser jsonParser = factory.createParser(file);
            System.out.println("Parsing .json file...");

            // parse header, indicating the table name
            jsonParser.nextToken();
            jsonParser.nextToken();
            jsonParser.nextToken();
            String tableName = jsonParser.getText();
            System.out.println("Table name: " + tableName);
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                ArrayList<String> fieldNames = new ArrayList<>();
                ArrayList<String> fieldValues = new ArrayList<>();
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                        String fieldName = jsonParser.getText();
                        fieldNames.add(fieldName);
                        jsonParser.nextToken(); // Move to the value of the field
                        String fieldValue = jsonParser.getText();
                        fieldValues.add(fieldValue);

                        // Process the name/value pair
                        System.out.println("Key: " + fieldName + ", Value: " + fieldValue);
                    }
                }
                // add to database
                String sql = "INSERT INTO " + tableName + " (";
                for (String s : fieldNames) {
                    sql += s + ",";
                }
                sql = sql.substring(0, sql.length() - 1); // remove last comma
                sql += ")";
                sql += " VALUES (";
                for (String s : fieldValues) {
                    sql += "?,";
                }
                sql = sql.substring(0, sql.length() - 1);
                sql += ")";
                System.out.println("SQL: " + sql);
                PreparedStatement ps = connection.prepareStatement(sql);
                int currPos = 1;
                for (String s : fieldValues) {
                    System.out.println("Set string at " + currPos);
                    ps.setString(currPos, s);
                    currPos++;
                }
                int result = ps.executeUpdate();
                if (result == 0) {
                    System.out.println("Nothing inserted");
                    return;
                }
                else {
                    System.out.println("Row inserted");
                }
            }
            jsonParser.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
