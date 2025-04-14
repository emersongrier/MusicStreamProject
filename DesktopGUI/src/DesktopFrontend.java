import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//import static com.BayWave.Tables.UserTable.passwordValid;
//import static com.BayWave.Tables.UserTable.usernameExists;
//import static java.sql.DriverManager.getConnection;

//import java.sql.SQLException;

public class DesktopFrontend extends Application {

        // private Stage window;
        private Scene mainScene, loginScene;
        
        @Override
        public void start(Stage primaryStage) {
                // SIGN IN LANDING PAGE //
                VBox loginPage = new VBox(10);
                loginPage.setPadding(new Insets(10));

                // Sign In page gradient background
                loginPage.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")), // Dark purple at the top
                                                new Stop(1, Color.web("#8756c8")) // Light purple at the bottom
                                ),
                                CornerRadii.EMPTY, Insets.EMPTY)));

                loginPage.setStyle("-fx-border-color: black;");
                
                File f = new File("C:\\Pictures\\Music Brand logo.jpg");
                Image img = new Image(f.toURI().toString());
                ImageView iview = new ImageView(img);
                iview.setFitWidth(50);
                iview.setFitHeight(50);
                iview.setPreserveRatio(true);
                Text loginTitle = new Text("Welcome!");
                loginTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
                loginTitle.setFill(Color.LIGHTGREEN);
                Text loginDesc = new Text("Please sign in to continue.");
                loginDesc.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                loginDesc.setFill(Color.LIGHTGREEN);
                Label userLabel = new Label("Username: ");
                userLabel.setStyle("-fx-text-fill: silver");
                TextField usernameInput = new TextField();
                usernameInput.setPromptText("Please enter your username here.");
                Label passLabel = new Label("Password: ");
                passLabel.setStyle("-fx-text-fill: silver");
                PasswordField passwordInput = new PasswordField();
                passwordInput.setPromptText("Please enter your password here.");
                Button signIn = new Button("Sign In");
                signIn.setAlignment(Pos.CENTER);
                Text errorMsg = new Text();
                loginPage.getChildren().addAll(iview, loginTitle, loginDesc, userLabel, usernameInput, passLabel, passwordInput, signIn, errorMsg);
        
                // MAIN PAGE //
                BorderPane root = new BorderPane();

                // Main Page Gradient Background
                root.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")),
                                                new Stop(1, Color.web("#8756c8"))),
                                CornerRadii.EMPTY, Insets.EMPTY)));

                // TOP
                HBox menuBar = new HBox(10);
                menuBar.setPadding(new Insets(10));
                menuBar.setMaxHeight(50);
                menuBar.setStyle("-fx-border-color: black;");
                Button homeButton = createImageButton("",
                                "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
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

                // LEFT
                VBox recents = new VBox(10);
                recents.setPadding(new Insets(10));
                recents.setStyle("-fx-border-color: black;");
                Button recent1 = createImageButton("Recent 1",
                                "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
                Button recent2 = createImageButton("Recent 2",
                                "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
                recent1.setMaxWidth(Double.MAX_VALUE);
                recent2.setMaxWidth(Double.MAX_VALUE);
                recents.getChildren().addAll(recent1, recent2);
                root.setLeft(recents);

                // CENTER
                // Search Results
                VBox searchResults = new VBox(10);
                searchResults.setPadding(new Insets(10));
                searchResults.setStyle("-fx-border-color: black;");
                Button result1 = createImageButton("Result 1",
                                "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
                Button result2 = createImageButton("Result 2",
                                "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png");
                result1.setMaxWidth(Double.MAX_VALUE);
                result2.setMaxWidth(Double.MAX_VALUE);
                searchResults.getChildren().addAll(result1, result2);
                root.setCenter(searchResults);
                homeButton.setOnAction(e -> root.setCenter(searchResults));

                // Profile Display
                VBox profileBox = new VBox(10);
                profileBox.setPadding(new Insets(10));
                profileBox.setStyle("-fx-border-color: black;");
                HBox profileBasics = new HBox();
                // insert profile picture
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

                // RIGHT
                VBox playingDetails = new VBox(10);
                playingDetails.setPadding(new Insets(10));
                playingDetails.setStyle("-fx-border-color: black;");
                ImageView albumcover = new ImageView(new Image(
                                "file:///" + "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png"
                                                .replace("\\", "/")));
                albumcover.setFitHeight(100);
                albumcover.setFitWidth(100);
                Label track = new Label("Track Name");
                Label artist = new Label("Artist Name");
                track.setStyle("-fx-text-fill: silver");
                artist.setStyle("-fx-text-fill: silver");
                playingDetails.getChildren().addAll(albumcover, track, artist);
                root.setRight(playingDetails);

                // BOTTOM
                HBox bottom = new HBox(10);
                bottom.setPadding(new Insets(10));
                bottom.setMaxHeight(50);
                bottom.setStyle("-fx-border-color: black;");
                ImageView smallAlbumCover = new ImageView(new Image(
                                "file:///" + "C:/Users/emcke/Pictures/Screenshots/Screenshot 2024-09-09 100439.png"
                                                .replace("\\", "/")));
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
                double percentSongOver = ((int) (356 / 409 * 100)) / 100;
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
                    if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {   
                        errorMsg.setText("ERROR: Please enter both, your username and password.");
                        errorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                        errorMsg.setFill(Color.RED);
                        return;// causes this program to fail to display the main page.
                    }  
                    
                    /*try {
                        if (usernameExists(getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;"), usernameInput.getText()) &&
                         passwordValid(getConnection("jdbc:h2:~/test;AUTOCOMMIT=OFF;"), usernameInput.getText(), passwordInput.getText())) {
                            // Now we know both credentials are correct, so the main page will now be displayed.
                            primaryStage.setScene(mainScene);
                            usernameInput.clear();
                            passwordInput.clear();
                        }

                        else {
                            errorMsg.setText("ERROR: Username or password does not exist.");
                            errorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                            errorMsg.setFill(Color.RED);
                            return; // causes this program to fail to display the main page.
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }*/
                    primaryStage.setScene(mainScene);
                    usernameInput.clear();
                    passwordInput.clear();
                });
        }

        public static void main(String[] args) {
                launch(args);
        }

        private Button createImageButton(String text, String imgPath) {
                ImageView icon = new ImageView(new Image("file:///" + imgPath.replace("\\", "/")));
                icon.setFitHeight(50);
                icon.setFitWidth(50);

                Button button = new Button(text, icon);
                return button;
        }
}
