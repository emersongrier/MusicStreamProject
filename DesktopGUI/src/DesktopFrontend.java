//command to run:
//java --module-path "C:\Users\emcke\Downloads\openjfx-21.0.6_windows-x64_bin-sdk\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,java.media -cp bin DesktopFrontend

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/*import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;*/
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.geometry.Side;

public class DesktopFrontend extends Application{

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
                /*loginPage.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")), // Dark purple at the top
                                                new Stop(1, Color.web("#8756c8")) // Light purple at the bottom
                                ),
                                CornerRadii.EMPTY, Insets.EMPTY)));*/

                loginPage.setStyle("-fx-border-color: black;");
                loginPage.setStyle("-fx-background-color: linear-gradient(to bottom, #001f3f, #003366);");
                Label userLabel = new Label("Username: ");
                userLabel.setStyle("-fx-text-fill: silver");
                TextField usernameInput = new TextField();
                Label passLabel = new Label("Password: ");
                passLabel.setStyle("-fx-text-fill: silver");
                TextField passwordInput = new TextField();
                Button signIn = new Button("Sign In");
                signIn.setAlignment(Pos.CENTER);
                loginPage.getChildren().addAll(userLabel, usernameInput, passLabel, passwordInput, signIn);

                // MAIN PAGE //
                StackPane sp = new StackPane();

                BorderPane root = new BorderPane();
                root.setStyle("-fx-background-color: linear-gradient(to bottom, #001f3f, #003366);");

                // Main Page Gradient Background
                /*root.setBackground(new Background(new BackgroundFill(
                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                new Stop(0, Color.web("#1a0033")),
                                                new Stop(1, Color.web("#8756c8"))),
                                CornerRadii.EMPTY, Insets.EMPTY)));*/

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
                addRippleEffect(createButton);
                Button profileButton = new Button(user.substring(0, 1));
                profileButton.setShape(new Circle(15));
                profileButton.setAlignment(Pos.CENTER_RIGHT);
                addRippleEffect(profileButton);
                menuBar.getChildren().addAll(homeButton, searchBar, createButton, profileButton);
                root.setTop(menuBar);

                ContextMenu createOptions = new ContextMenu();
                MenuItem createPlaylist = new MenuItem("Create Playlist");
                MenuItem openDAW = new MenuItem("Add Filter");
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
                Button recent1 = createImageButton("Recent Album", defaultMusicImage);
                Button recent2 = createImageButton("Recent Playlist", defaultMusicImage);
                recent1.setMaxWidth(Double.MAX_VALUE);
                recent2.setMaxWidth(Double.MAX_VALUE);
                recents.getChildren().addAll(recent1, recent2);
                root.setLeft(recents);

                // CENTER
                // Search Results
                VBox searchResults = new VBox(10);
                searchResults.setPadding(new Insets(10));
                searchResults.setStyle("-fx-border-color: black;");
                Button result1 = createImageButton("Search Result", defaultMusicImage);
                Button result2 = createImageButton("Search Result", defaultMusicImage);
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
                Label profileName = new Label(user);
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
                ImageView albumcover = new ImageView(new Image(getClass().getResourceAsStream(currentAlbumCover)));
                albumcover.setPreserveRatio(true);
                albumcover.setFitWidth(140);
                Label track, artist;
                if(currentSong.length() < 25){
                        track = new Label(currentSong);
                } else {
                        track = new Label(currentSong.substring(0, 20) + "...");
                }
                if(currentArtist.length() < 30){
                        artist = new Label(currentArtist);
                } else {
                        artist = new Label(currentArtist.substring(0, 25) + "...");
                }
                track.setStyle("-fx-text-fill: silver");
                artist.setStyle("-fx-font-size: 12; -fx-text-fill: silver");
                playingDetails.getChildren().addAll(albumcover, track, artist);
                root.setRight(playingDetails);

                // BOTTOM
                HBox bottom = new HBox(10);
                bottom.setPadding(new Insets(10));
                bottom.setMaxHeight(50);
                bottom.setStyle("-fx-border-color: black;");
                ImageView smallAlbumCover = new ImageView(new Image(getClass().getResourceAsStream(currentAlbumCover)));
                smallAlbumCover.setFitHeight(80);
                smallAlbumCover.setPreserveRatio(true);
                VBox trackDesc = new VBox(10);
                trackDesc.setPadding(new Insets(10));
                Label trackPlaying, artistPlaying; // = new Label(currentSong);
                //Label artistPlaying = new Label(currentArtist);
                if(currentSong.length() < 30){
                        trackPlaying = new Label(currentSong);
                } else {
                        trackPlaying = new Label(currentSong.substring(0, 25) + "...");
                }
                if(currentArtist.length() < 40){
                        artistPlaying = new Label(currentArtist);
                } else {
                        artistPlaying = new Label(currentArtist.substring(0, 35) + "...");
                }
                trackPlaying.setStyle("-fx-font-size: 12px; -fx-text-fill: silver");
                artistPlaying.setStyle("-fx-font-size: 10px; -fx-text-fill: silver");
                trackDesc.getChildren().addAll(trackPlaying, artistPlaying);
                trackDesc.setAlignment(Pos.CENTER_LEFT);
                VBox playback = new VBox(10);
                playback.setPadding(new Insets(10));
                HBox controls = new HBox(10);
                controls.setPadding(new Insets(10));
                Button back = new Button("\u23ee");
                addRippleEffect(back);
                Button plause = new Button("\u23f5");
                addRippleEffect(plause);
                Button forward = new Button("\u23ed");
                addRippleEffect(forward);
                controls.getChildren().addAll(back, plause, forward);
                controls.setAlignment(Pos.CENTER);
                HBox trackProgress = new HBox(10);
                trackProgress.setPadding(new Insets(10));
                timeElapsed = new Label();
                timeElapsed.setStyle("-fx-text-fill: silver");
                ProgressBar progress = new ProgressBar();
                new ProgressThread(progress).start();
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
                trackLength = new Label();
                trackLength.setStyle("-fx-text-fill: silver");
                trackProgress.getChildren().addAll(timeElapsed, progress, trackLength);
                trackProgress.setAlignment(Pos.CENTER);
                playback.getChildren().addAll(controls, trackProgress);
                bottom.getChildren().addAll(smallAlbumCover, trackDesc, playback);
                root.setBottom(bottom);
                //root.setMargin(bottom, new Insets(0));

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
                                new ProgressThread(progress).start();
                                mediaPlayer.play();
                        }
                });

                //SCENE SETTING
                applyWaveAnimation(root);

                sp.getChildren().addAll(root, listView);
                loginScene = new Scene(loginPage, 1000, 600);
                loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                mainScene = new Scene(sp, 1000, 600);
                mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                primaryStage.setScene(loginScene);
                primaryStage.setTitle("UI");
                primaryStage.show();

                signIn.setOnAction(e -> {
                        primaryStage.setScene(mainScene);
                        usernameInput.clear();
                        passwordInput.clear();
                });
        }

        public static void main(String[] args) {
                launch(args);
        }

        /**
         * creates a button that has text and an image together
         * @param text text to appear in the button
         * @param imgPath location of the image to appear in the image
         * @return returns the button that can then be added to whatever kind of frame
         */
        private Button createImageButton(String text, String imgPath) {
                ImageView icon = new ImageView(new Image(imgPath));
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
        private void applyWaveAnimation(Region region) {
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

                public ProgressThread(ProgressBar progressBar) {
                        this.progressBar = progressBar;
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

