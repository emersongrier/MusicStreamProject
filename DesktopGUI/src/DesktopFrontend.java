//command to run:
//java --module-path "C:\Users\emcke\MusicStreamProject\DesktopGUI\src\resources\openjfx-21.0.6_windows-x64_bin-sdk\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -cp bin DesktopFrontend

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javax.swing.plaf.synth.Region;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.scene.effect.ColorAdjust;
import java.io.File;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
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
        private static double songElapsed, songLength;
        private static String currentSong, currentArtist, currentAlbumCover, currentSongPath; 
        private static Label timeElapsed, trackLength;
        private static boolean playing = false;

        private final List<String> songList = List.of(
                "022.mp3", "Invariance.mp3", "8bit Dungeon Boss.mp3", "Investigations.mp3",
                "8bit Dungeon Level.mp3", "Iron Bacon.mp3", "A Little Faith.mp3", "Iron Horse - Distressed.mp3",
                "A Mission.mp3", "Iron Horse.mp3", "A Singular Perversion.mp3", "Irregular.mp3",
                "A Turn for the Worse.mp3", "Ishikari Lore.mp3", "A Very Brady Special.mp3", "Island Meet and Greet.mp3",
                "Accralate.mp3", "Isolated.mp3", "Aces High.mp3", "It Came Upon a Midnight Clear.mp3",
                "Achaidh Cheide.mp3", "It is Lost.mp3", "Achilles.mp3", "Itty Bitty 8 Bit.mp3",
                "AcidJazz.mp3", "Jalandhar.mp3", "Action.mp3", "Jarvic 8.mp3",
                "Adding the Sun.mp3", "Jaunty Gumption.mp3", "Adventure Meme.mp3", "Jazz Brunch.mp3",
                "Aftermath.mp3", "Jellyfish in Space.mp3", "Aggressor.mp3", "Jerry Five.mp3",
                "Agnus Dei X.mp3", "Jet Fueled Vixen.mp3", "AhDah.mp3", "Jingle Bells 3.mp3",
                "Air Prelude.mp3", "Jingle Bells Calm.mp3", "Airport Lounge.mp3", "Jingle Bells.mp3",
                "Airship Serenity.mp3", "Juniper.mp3", "Alchemists Tower.mp3", "Junkyard Tribe.mp3",
                "Alien Restaurant.mp3", "Just As Soon.mp3", "All This.mp3", "Just Nasty.mp3"
        );

        @Override
        public void start(Stage primaryStage) {
                Font.loadFont(getClass().getResourceAsStream("/resources/fonts/smooth_line_7.ttf"), 12);

                //sample initialization of global variables
                currentArtist = "Riley Simmons";
                currentSong = "A Giant Goliath Ate My Car!";
                currentSongPath = "Jalandhar.mp3";
                currentAlbumCover = "/resources/images/aGiantGoliathAteMyCar.jpg";
                String defaultMusicImage = "/resources/images/defaultMusicPlaying.png";
                songLength = 88615;
                songElapsed = 70000;
                String user = "Eli McKercher";
                
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

                // CREATE ACCOUNT LANDING PAGE //
                VBox createAccountPage = new VBox(10);
                createAccountPage.setPadding(new Insets(10));

                // Create Account page gradient background
                createAccountPage.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")), // Dark purple at the top
                                                new Stop(1, Color.web("#8756c8")) // Light purple at the bottom
                                ),
                                CornerRadii.EMPTY, Insets.EMPTY)));

                createAccountPage.setStyle("-fx-border-color: black;");
                Text createAccountTitle = new Text("Create Your Account");
                createAccountTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 35));
                createAccountTitle.setFill(Color.LIGHTGREEN);
                
                Text importantNote = new Text("PLEASE NOTE: The asterisk (*) in front of the label"
                + " denotes this field is required to be entered in.");
                importantNote.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                importantNote.setFill(Color.RED);
                
                Label newUsernameLabel = new Label("*Username: ");
                newUsernameLabel.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                TextField newUsernameInput = new TextField();
                newUsernameInput.setPromptText("REQUIRED: Please enter your username here.");
                newUsernameInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Label newPasswordLabel = new Label("*Password: ");
                newPasswordLabel.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                PasswordField newPasswordInput = new PasswordField();
                newPasswordInput.setPromptText("REQUIRED: Please enter your password here.");
                newPasswordInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Label confirmPasswordLabel = new Label("*Confirm Password: ");
                confirmPasswordLabel.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                PasswordField confirmPasswordInput = new PasswordField();
                confirmPasswordInput.setPromptText("REQUIRED: Please confirm your password here.");
                confirmPasswordInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Label newEmailLabel = new Label("*Email: ");
                newEmailLabel.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                TextField newEmailInput = new TextField();
                newEmailInput.setPromptText("REQUIRED: Please enter your email here (e.g. someone@example.com).");
                newEmailInput.setStyle("-fx-prompt-text-fill: purple;");
                
                // Assume the user's phone number will be formatted in USA format, i.e. +1 (XXX)-XXX-XXXX.
                Label newPhoneLabel = new Label("Phone: ");
                newPhoneLabel.setStyle("-fx-text-fill: silver");
                TextField newPhoneInput = new TextField();
                newPhoneInput.setPromptText("Please enter your phone number, e.g. +1 (XXX)-XXX-XXXX.");
                newPhoneInput.setStyle("-fx-prompt-text-fill: purple;");
                
                CheckBox checkEULA = new CheckBox("*I agree to"
                + " accept the End User License Agreement and Privacy Policy.");
                checkEULA.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                        
                Button createAccount = new Button("Create Account");
                createAccount.setAlignment(Pos.CENTER);
                
                Text createAccountErrorMsg = new Text();
                
                createAccount.setOnAction(e -> {
                    if (newUsernameInput.getText().isEmpty() || 
                            (newPasswordInput.getText().isEmpty() || confirmPasswordInput.getText().isEmpty()) || 
                            newEmailInput.getText().isEmpty()) {
                        createAccountErrorMsg.setText("ERROR: Missing username, email, or password.");
                        createAccountErrorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                        createAccountErrorMsg.setFill(Color.RED);
                        return;
                    }
                    
                    if ( !newPasswordInput.getText().equals( confirmPasswordInput.getText() ) ){
                        Alert errorDialog = new Alert(AlertType.ERROR, "Password fields do not match. Try again!", ButtonType.OK);
                        errorDialog.showAndWait();
                        return;
                    }
                    
                    if ( !checkEULA.isSelected()){
                        Alert errorDialog = new Alert(AlertType.ERROR, "You MUST accept the End User License Agreement", ButtonType.OK);
                        errorDialog.showAndWait();
                        return;
                    }
                    primaryStage.setScene(accountCreationSuccessScene);
                    newUserIDInput.clear();
                    newUsernameInput.clear();
                    newPasswordInput.clear();
                    confirmPasswordInput.clear();
                    newEmailInput.clear();
                    newUserFullNameInput.clear();
                    newPhoneInput.clear();
                    birthdateSelect.getEditor().clear();
                    radioMale.setSelected(false);
                    radioFemale.setSelected(false);
                    checkOptIn.setSelected(false);
                    checkEULA.setSelected(false);
                });
                

                
                Hyperlink toSignInPage = new Hyperlink("Please click here if you already have an account.");
                toSignInPage.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue");
                toSignInPage.setOnAction(e -> {
                    primaryStage.setScene(loginScene);
                    newUserIDInput.clear();
                    newUsernameInput.clear();
                    newPasswordInput.clear();
                    confirmPasswordInput.clear();
                    newEmailInput.clear();
                    newUserFullNameInput.clear();
                    newPhoneInput.clear();
                    birthdateSelect.getEditor().clear();
                    radioMale.setSelected(false);
                    radioFemale.setSelected(false);
                    checkOptIn.setSelected(false);
                    checkEULA.setSelected(false);
                });
                createAccountPage.getChildren().addAll(createAccountTitle, importantNote, newUserIDLabel, newUserIDInput, newUsernameLabel, newUsernameInput);
                createAccountPage.getChildren().addAll(newPasswordLabel, newPasswordInput, confirmPasswordLabel, confirmPasswordInput, newEmailLabel, newEmailInput);
                createAccountPage.getChildren().addAll(newUserFullNameLabel, newUserFullNameInput, newPhoneLabel, newPhoneInput, birthdayLabel, birthdateSelect);
                createAccountPage.getChildren().addAll(genderLabel, radioMale, radioFemale, checkOptIn, checkEULA, createAccount, createAccountErrorMsg, toSignInPage);
                
                //Account Creation successful
                VBox accountCreationSuccessPage = new VBox(10);
                accountCreationSuccessPage.setPadding(new Insets(10));

                // Account Creation successful page gradient background
                accountCreationSuccessPage.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")), // Dark purple at the top
                                                new Stop(1, Color.web("#8756c8")) // Light purple at the bottom
                                ),
                                CornerRadii.EMPTY, Insets.EMPTY)));

                accountCreationSuccessPage.setStyle("-fx-border-color: black;");
                
                File imageFile2 = new File("C:\\Pictures\\Success.jpg");
                Image img2 = new Image(imageFile2.toURI().toString());
                ImageView checkmarkImage = new ImageView(img2);
                checkmarkImage.setFitWidth(50);
                checkmarkImage.setFitHeight(50);
                checkmarkImage.setPreserveRatio(true);
                
                Text successfulMessage = new Text("Congratulations! You now have successfully created your account.");
                successfulMessage.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                successfulMessage.setFill(Color.LIGHTGREEN);
                
                Hyperlink backToSignInPage = new Hyperlink("Please click here to go back to the Sign-in page.");
                backToSignInPage.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue");
                backToSignInPage.setOnAction(e -> {
                    primaryStage.setScene(loginScene);
                });
                accountCreationSuccessPage.getChildren().addAll(checkmarkImage, successfulMessage, backToSignInPage);
        
                // MAIN PAGE //
                StackPane sp = new StackPane();

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
                Button homeButton = new Button("\u2302");
                homeButton.setMaxSize(50, 50);
                addRippleEffect(homeButton);
                //VBox searchBarResults = new VBox(10);
                TextField searchBar = new TextField();
                searchBar.setAlignment(Pos.CENTER);
                ListView<String> listView = new ListView<>();
                ObservableList<String> filteredList = FXCollections.observableArrayList(songList);
                listView.setItems(filteredList);
                listView.setVisible(false);
                listView.setPrefHeight(96); // ~4 rows * 24px row height
                listView.setMaxHeight(96);
                listView.setFixedCellSize(24);
                searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
                        List<String> matches = songList.stream()
                                .filter(song -> song.toLowerCase().contains(newVal.toLowerCase()))
                                .collect(Collectors.toList());
                        filteredList.setAll(matches);
                        listView.setVisible(!matches.isEmpty() && !newVal.isEmpty());
                });
                //searchBarResults.getChildren().addAll(searchBar, listView);
                //searchBar.setStyle("-fx-background-color: lightsteelblue;;");
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
                Button back = new Button("\u23ee");
                Button plause = new Button("\u23f8");
                Button forward = new Button("\u23ed");
                controls.getChildren().addAll(back, plause, forward);
                controls.setAlignment(Pos.CENTER);
                HBox trackProgress = new HBox(10);
                trackProgress.setPadding(new Insets(10));
                timeElapsed = new Label("5:56");
                timeElapsed.setStyle("-fx-text-fill: silver");
                ProgressBar progress = new ProgressBar();
                new ProgressThread(progress, songElapsed, songLength).start();
                //progress.setStyle("-fx-accent: green;");
                progress.setVisible(true);
                progress.setManaged(true);
                Timeline PBTimeline = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(progress.styleProperty(), "-fx-background-position: 0 0;")),
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(progress.styleProperty(), "-fx-background-position: -50px 0;"))
                );
                PBTimeline.setCycleCount(Animation.INDEFINITE);
                PBTimeline.play();
                trackLength = new Label("6:49");
                trackLength.setStyle("-fx-text-fill: silver");
                trackProgress.getChildren().addAll(timeElapsed, progress, trackLength);
                trackProgress.setAlignment(Pos.CENTER);
                playback.getChildren().addAll(controls, trackProgress);
                bottom.getChildren().addAll(smallAlbumCover, trackDesc, playback);
                root.setBottom(bottom);

                MusicClient client = new MusicClient();
                Path songPath = null;
                try {
                        songPath = client.downloadSong(currentSongPath);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                Media media = new Media(songPath.toUri().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                plause.setOnAction(e ->{
                        if(playing){
                                plause.setText("\u23f5");
                                playing = false;
                                mediaPlayer.pause();
                        }
                        else{
                                plause.setText("\u23f8");
                                playing = true;
                                new ProgressThread(progress, songElapsed, songLength).start();
                                mediaPlayer.play();
                        }
                });

                //SCENE SETTING
                applyWaveAnimation((Node)root);

                sp.getChildren().addAll(root, listView);
                loginScene = new Scene(loginPage, 1000, 600);
                loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                mainScene = new Scene(sp, 1000, 600);
                mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                primaryStage.setScene(loginScene);
                primaryStage.setTitle("UI");
                primaryStage.show();

                signIn.setOnAction(e -> {
                    /*if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {   
                        errorMsg.setText("ERROR: Please enter both, your username and password.");
                        errorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                        errorMsg.setFill(Color.RED);
                        return;// causes this program to fail to display the main page.
                    } */ 
                    
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

        /**
         * adds animation to the botton called upon clicking by darking and lightening it
         * @param button button to be given the animation
         */
        private void addRippleEffect(Button button) {
                ColorAdjust colorAdjust = new ColorAdjust();
                button.setEffect(colorAdjust);

                Timeline rippleTimeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(400), new KeyValue(colorAdjust.brightnessProperty(), -0.3, Interpolator.EASE_BOTH)) // Darken smoothly
                );

                rippleTimeline.setAutoReverse(true);
                rippleTimeline.setCycleCount(2); // One forward, one backward
                button.setOnMousePressed(e -> rippleTimeline.play());
                //button.setOnMouseReleased(e -> ri4pleTimeline.stop());
        }

        /**
         * Causes the background (of whatever tier) to slowly pulse by changing opacity
         * @param region scene, vbox, ... what should pulse
         */
        private void applyWaveAnimation(Node region) {
                Timeline waveTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(region.opacityProperty(), 0.9)),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(region.opacityProperty(), 1.0))
                );
                waveTimeline.setCycleCount(Animation.INDEFINITE);
                waveTimeline.setAutoReverse(true);
                waveTimeline.play();
        }

         //------------------------------------------------------------------------------------------------------
        /**
         * This class/thread is meant to handle updating the progress bar as time goes on
         */
        static class ProgressThread extends Thread {
                private final ProgressBar progressBar;
                private final double songElapsed;
                private final double songLength;

                public ProgressThread(ProgressBar progressBar, double songElapsed, double songLength) {
                        this.progressBar = progressBar;
                        this.songElapsed = songElapsed;
                        this.songLength = songLength;
                }

                @Override
                public void run() {
                        while(playing){
                                Platform.runLater(() -> progressBar.setProgress(songElapsed/songLength));
                                Platform.runLater(() -> timeElapsed.setText(
                                        ((int) songElapsed)/1000/60 + ":" + ((int) songElapsed)/1000%60));
                                Platform.runLater(() -> trackLength.setText(
                                        ((int) songLength)/1000/60 + ":" + ((int) songLength)/1000%60));
                                try {
                                        Thread.sleep(500);
                                } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                        }
                        System.out.println("terminating thread");
                }
        }
}
