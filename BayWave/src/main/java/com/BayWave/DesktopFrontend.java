package com.BayWave;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.geometry.Side;

import static com.BayWave.Tables.UserTable.passwordValid;
import static com.BayWave.Tables.UserTable.usernameExists;
import static java.sql.DriverManager.getConnection;

import java.sql.SQLException;

public class DesktopFrontend extends Application {

    //private Stage window;
    private Scene mainScene, loginScene;

    @Override
    public void start(Stage primaryStage) {
        //SIGN IN LANDING PAGE
        VBox loginPage = new VBox(10);
        loginPage.setPadding(new Insets(10));
        loginPage.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        Label userLabel = new Label("Username: ");
        userLabel.setStyle("-fx-text-fill: silver");
        TextField usernameInput = new TextField();
        Label passLabel = new Label("Password: ");
        passLabel.setStyle("-fx-text-fill: silver");
        TextField passwordInput = new TextField();
        Button signIn = new Button("Sign In");
        signIn.setAlignment(Pos.CENTER);
        Label errorMsg = new Label();
        loginPage.getChildren().addAll(userLabel, usernameInput, passLabel, passwordInput, signIn, errorMsg);


        //MAIN PAGE
        BorderPane root = new BorderPane();

        //TOP
        HBox menuBar = new HBox(10);
        menuBar.setPadding(new Insets(10));
        menuBar.setMaxHeight(50);
        menuBar.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        Button homeButton = createImageButton("", "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
        homeButton.setMaxSize(10, 10);
        TextField searchBar = new TextField();
        searchBar.setAlignment(Pos.CENTER);
        Button createButton = new Button("+");
        createButton.setShape(new Circle(15));
        Button profileButton = new Button("E");
        profileButton.setShape(new Circle(15));
        profileButton.setAlignment(Pos.CENTER_RIGHT);
        menuBar.getChildren().addAll(homeButton, searchBar, createButton, profileButton);
        root.setTop(menuBar);

        ContextMenu createOptions = new ContextMenu();
        MenuItem createPlaylist = new MenuItem("Create Playlist");
        MenuItem openDAW = new MenuItem("Create Track");
        createOptions.getItems().addAll(createPlaylist, openDAW);
        createButton.setOnAction(e -> createOptions.show(createButton, Side.BOTTOM, 0, 0));

        ContextMenu profileBtnDD = new ContextMenu();
        MenuItem viewProfile = new MenuItem("View Profile");
        MenuItem logOut = new MenuItem("Log Out");
        profileBtnDD.getItems().addAll(viewProfile, logOut);
        profileButton.setOnAction(e -> profileBtnDD.show(profileButton, Side.BOTTOM, 0, 0));
        logOut.setOnAction(e -> primaryStage.setScene(loginScene));


        //LEFT
        VBox recents = new VBox(10);
        recents.setPadding(new Insets(10));
        recents.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        Button recent1 = createImageButton("Recent 1", "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
        Button recent2 = createImageButton("Recent 2", "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
        recent1.setMaxWidth(Double.MAX_VALUE);
        recent2.setMaxWidth(Double.MAX_VALUE);
        recents.getChildren().addAll(recent1, recent2);
        root.setLeft(recents);

        //CENTER
        //Search Results
        VBox searchResults = new VBox(10);
        searchResults.setPadding(new Insets(10));
        searchResults.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        Button result1 = createImageButton("Result 1", "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
        Button result2 = createImageButton("Result 2", "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
        result1.setMaxWidth(Double.MAX_VALUE);
        result2.setMaxWidth(Double.MAX_VALUE);
        searchResults.getChildren().addAll(result1, result2);
        root.setCenter(searchResults);
        homeButton.setOnAction(e -> root.setCenter(searchResults));

        //Profile Display
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(10));
        profileBox.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        HBox profileBasics = new HBox();
        //insert profile picture
        Label profileName = new Label("accountName");
        profileName.setStyle("-fx-text-fill: silver");
        profileBasics.getChildren().addAll(profileName);
        HBox socialStatus = new HBox();
        Label followers = new Label("Followers: ");
        followers.setStyle("-fx-text-fill: silver");
        Label following = new Label("Following: ");
        following.setStyle("-fx-text-fill: silver");
        socialStatus.getChildren().addAll(followers, following);
        profileBox.getChildren().addAll(profileBasics, socialStatus);

        viewProfile.setOnAction(e -> root.setCenter(profileBox));


        //RIGHT
        VBox playingDetails = new VBox(10);
        playingDetails.setPadding(new Insets(10));
        playingDetails.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        ImageView albumcover = new ImageView(new Image("file:///" + "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png".replace("\\", "/")));
        albumcover.setFitHeight(100);
        albumcover.setFitWidth(100);
        Label track = new Label("Track Name");
        Label artist = new Label("Artist Name");
        track.setStyle("-fx-text-fill: silver");
        artist.setStyle("-fx-text-fill: silver");
        playingDetails.getChildren().addAll(albumcover, track, artist);
        root.setRight(playingDetails);

        //BOTTOM
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10));
        bottom.setMaxHeight(50);
        bottom.setStyle("-fx-background-color: #2f0068; -fx-border-color: black;");
        ImageView smallAlbumCover = new ImageView(new Image("file:///" + "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png".replace("\\", "/")));
        smallAlbumCover.setFitHeight(30);
        smallAlbumCover.setFitWidth(30);
        VBox trackDesc = new VBox(10);
        trackDesc.setPadding(new Insets(10));
        Label trackPlaying = new Label("Track Name");
        Label artistPlaying = new Label("Artist Name");
        trackPlaying.setStyle("-fx-font-size: 10px; -fx-text-fill: silver");
        artistPlaying.setStyle("-fx-font-size: 10px; -fx-text-fill: silver");
        trackDesc.getChildren().addAll(trackPlaying, artistPlaying);
        trackDesc.setAlignment(Pos.CENTER);
        VBox playback = new VBox(10);
        playback.setPadding(new Insets(10));
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        Button back = new Button("<");
        Button plause = new Button("||");
        Button forward = new Button(">");
        controls.getChildren().addAll(back, plause, forward);
        controls.setAlignment(Pos.CENTER);
        HBox trackProgress = new HBox(10);
        trackProgress.setPadding(new Insets(10));
        Label timeElapsed = new Label("5:56");
        timeElapsed.setStyle("-fx-text-fill: silver");
        double percentSongOver = ((int)(356/409*100))/100;
        ProgressBar progress = new ProgressBar(percentSongOver);
        progress.setStyle("-fx-accent: green;");
        Label trackLength = new Label("6:49");
        trackLength.setStyle("-fx-text-fill: silver");
        trackProgress.getChildren().addAll(timeElapsed, progress, trackLength);
        trackProgress.setAlignment(Pos.CENTER);
        playback.getChildren().addAll(controls, trackProgress);
        bottom.getChildren().addAll(smallAlbumCover, trackDesc, playback);
        root.setBottom(bottom);

        loginScene = new Scene(loginPage, 600, 600);
        mainScene = new Scene(root, 600, 600);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("UI");
        primaryStage.show();

        signIn.setOnAction(e -> {
            try {
                if (usernameExists(getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;"), usernameInput.getText()) &&
                 passwordValid(getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;"), usernameInput.getText(), passwordInput.getText()))
                {
                    primaryStage.setScene(mainScene);
                    usernameInput.clear();
                    passwordInput.clear();
                }
                else errorMsg.setText("ERROR: Username or password does not exist.");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Button createImageButton(String text, String imgPath){
        ImageView icon = new ImageView(new Image("file:///" + imgPath.replace("\\", "/")));
        icon.setFitHeight(50);
        icon.setFitWidth(50);

        Button button = new Button(text, icon);
        return button;
    }
}

