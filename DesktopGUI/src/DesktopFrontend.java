//command to run:
//java --module-path "C:\Users\emcke\MusicStreamProject\DesktopGUI\src\resources\openjfx-21.0.6_windows-x64_bin-sdk\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,com.google.gson -cp bin DesktopFrontend
//javac --module-path "C:\Users\emcke\MusicStreamProject\DesktopGUI\src\resources\openjfx-21.0.6_windows-x64_bin-sdk\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media,com.google.gson -d bin src/*.java

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javax.swing.plaf.synth.Region;
import javax.swing.text.html.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.scene.effect.ColorAdjust;
import java.io.File;
import java.io.FileInputStream;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.PauseTransition;
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
import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import com.google.gson.*;
import javax.net.ssl.HttpsURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Optional;
import javafx.scene.image.PixelReader;

//import static com.BayWave.Tables.UserTable.passwordValid;
//import static com.BayWave.Tables.UserTable.usernameExists;
//import static java.sql.DriverManager.getConnection;

//import java.sql.SQLException;

public class DesktopFrontend extends Application {

        // private Stage window;
        private Scene mainScene, loginScene, createAccountScene, accountCreationSuccessScene;
        private static double songElapsed, songLength;
        private static String currentSong, currentArtist, logo, songID, username, password, metadata; 
        private static Label timeElapsed, trackLength, trackPlaying, artistPlaying, track, artist;
        private static boolean playing, ambianceStatus = false;
        private static MediaPlayer mediaPlayer = null;
        private static Button plause, profileButton = null;
        private static ProgressBar progress = null;
        private static Path songPath = null;
        private static Media media = null;
        private static MediaPlayer ambiancePlayer = null;

        @Override
        public void start(Stage primaryStage) {
                Font.loadFont(getClass().getResourceAsStream("/resources/fonts/smooth_line_7.ttf"), 12);

                //sample initialization of global variables
                currentArtist = "Kevin Macleod";
                currentSong = "Jazz Brunch";
                logo = "/resources/images/logo.png";
                String defaultMusicImage = "/resources/images/defaultMusicPlaying.png";
                String colorWheelPath = "C:/Users/emcke/MusicStreamProject/DesktopGUI/src/resources/images/color_wheel.png";
                songID = "879";
                songLength = 88615;
                songElapsed = 0;
                MusicClient client = new MusicClient();
                JsonArray songMetadata = new JsonArray();
                Color defaultDarkPurple = Color.web("#1a0033");
                Color defaultLightPurple = Color.web("#8756c8");
                
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
                
                File f = new File(logo);
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
                Hyperlink toCreateAccountPage = new Hyperlink("Please click here if you have not created an account.");
                toCreateAccountPage.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue; -fx-cursor: hand;");
                loginPage.getChildren().addAll(iview, loginTitle, loginDesc, userLabel, usernameInput, passLabel, passwordInput, signIn, errorMsg, toCreateAccountPage);

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
                
                Label newEmailLabel = new Label("Email: ");
                newEmailLabel.setStyle("-fx-text-fill: silver; -fx-font-weight: BOLD");
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
                                (newPasswordInput.getText().isEmpty() || confirmPasswordInput.getText().isEmpty())) {
                                createAccountErrorMsg.setText("ERROR: Missing username or password.");
                                createAccountErrorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                                createAccountErrorMsg.setFill(Color.RED);
                                return;
                        }else if ( !newPasswordInput.getText().equals( confirmPasswordInput.getText() ) ){
                                Alert errorDialog = new Alert(AlertType.ERROR, "Password fields do not match. Try again!", ButtonType.OK);
                                errorDialog.showAndWait();
                                return;
                        }else if ( !checkEULA.isSelected()){
                                Alert errorDialog = new Alert(AlertType.ERROR, "You MUST accept the End User License Agreement", ButtonType.OK);
                                errorDialog.showAndWait();
                                return;
                        }
                        boolean accountCreateResult = client.createAccount(newUsernameInput.getText(), newPasswordInput.getText());
                        if (!accountCreateResult){
                                Alert errorDialog = new Alert(AlertType.ERROR, "Account creation failed. Try again!", ButtonType.OK);
                                errorDialog.showAndWait();
                                return;
                        }
                        username = newUsernameInput.getText();
                        password = newPasswordInput.getText();
                        primaryStage.setScene(accountCreationSuccessScene);
                        newUsernameInput.clear();
                        newPasswordInput.clear();
                        confirmPasswordInput.clear();
                        newEmailInput.clear();
                        newPhoneInput.clear();
                        checkEULA.setSelected(false);
                });
                
                toCreateAccountPage.setOnAction(e -> {
                        primaryStage.setScene(createAccountScene);
                });
                
                Hyperlink toSignInPage = new Hyperlink("Please click here if you already have an account.");
                toSignInPage.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue");
                toSignInPage.setOnAction(e -> {
                    primaryStage.setScene(loginScene);
                    newUsernameInput.clear();
                    newPasswordInput.clear();
                    confirmPasswordInput.clear();
                    newEmailInput.clear();
                    newPhoneInput.clear();
                    checkEULA.setSelected(false);
                });
                createAccountPage.getChildren().addAll(createAccountTitle, importantNote, newUsernameLabel, newUsernameInput);
                createAccountPage.getChildren().addAll(newPasswordLabel, newPasswordInput, confirmPasswordLabel, confirmPasswordInput, newEmailLabel, newEmailInput);
                createAccountPage.getChildren().addAll(newPhoneLabel, newPhoneInput);
                createAccountPage.getChildren().addAll(checkEULA, createAccount, createAccountErrorMsg, toSignInPage);
                
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
                Button createButton = new Button("+");
                createButton.setShape(new Circle(15));
                profileButton = new Button();
                profileButton.setShape(new Circle(15));
                profileButton.setAlignment(Pos.CENTER_RIGHT);
                menuBar.getChildren().addAll(homeButton, searchBar, createButton, profileButton);
                root.setTop(menuBar);

                ContextMenu createOptions = new ContextMenu();
                MenuItem createPlaylist = new MenuItem("Create Playlist");
                MenuItem openDAW = new MenuItem("Add Filter");
                MenuItem ambiancePlay = new MenuItem("Toggle Ambiance");
                createOptions.getItems().addAll(createPlaylist, openDAW, ambiancePlay);
                createButton.setOnAction(e -> createOptions.show(createButton, Side.BOTTOM, 0, 0));

                ContextMenu profileBtnDD = new ContextMenu();
                MenuItem viewSettings = new MenuItem("View Setting");
                MenuItem viewProfile = new MenuItem("View Profile");
                MenuItem logOut = new MenuItem("Log Out");
                profileBtnDD.getItems().addAll(viewSettings, viewProfile, logOut);
                profileButton.setOnAction(e -> profileBtnDD.show(profileButton, Side.BOTTOM, 0, 0));
                logOut.setOnAction(e -> primaryStage.setScene(loginScene));

                ambiancePlay.setOnAction(e ->{
                        if(!ambianceStatus){
                                try{
                                        URL url = new URI("https://baywave.org:8080/ambience?type=rain").toURL();
                                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        Path ambiancePath;
                                        try (InputStream in = conn.getInputStream()) {
                                                ambiancePath = Files.createTempFile("rainAmbiance", null);
                                                Files.copy(in, ambiancePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                        }
                                        Media ambianceMedia = new Media(ambiancePath.toUri().toString());
                                        ambiancePlayer = new MediaPlayer(ambianceMedia);
                                        ambiancePlayer.setVolume(0.04);
                                        ambiancePlayer.play();
                                        ambianceStatus = true;
                                        ambiancePlayer.setOnEndOfMedia(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ambiancePlayer.seek(Duration.ZERO);
                                                    ambiancePlayer.play();
                                                }
                                            }); 
                                } catch(Exception h){
                                        h.printStackTrace();
                                }
                                
                        }
                        else{
                                ambiancePlayer.pause();
                                ambianceStatus = false;
                        }
                });
                

                // LEFT
                VBox recents = new VBox(10);
                recents.setPadding(new Insets(10));
                recents.setStyle("-fx-border-color: black;");
                ImageView defaultImageView = new ImageView(defaultMusicImage);
                defaultImageView.setFitHeight(75);
                defaultImageView.setFitWidth(75);
                ImageView defaultImageView2 = new ImageView(defaultMusicImage);
                defaultImageView2.setFitHeight(75);
                defaultImageView2.setFitWidth(75);
                Button recent1 = new Button("Recent 1", defaultImageView);
                Button recent2 = new Button("Recent 2", defaultImageView2);
                recent1.setMaxWidth(Double.MAX_VALUE);
                recent2.setMaxWidth(Double.MAX_VALUE);
                recents.getChildren().addAll(recent1, recent2);
                root.setLeft(recents);

                // CENTER
                // Search Results
                VBox searchResults = new VBox(10);
                searchResults.setPadding(new Insets(10));
                searchResults.setStyle("-fx-border-color: black;");
                ContextMenu meatballMenu = new ContextMenu();
                MenuItem songToPlaylist = new MenuItem("Add to Playlist");
                MenuItem likeSong = new MenuItem("Like Song");
                meatballMenu.getItems().addAll(songToPlaylist, likeSong);
                searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
                        JsonArray json = new JsonArray();
                        try {
                                json = JsonParser.parseString(client.searchDb(newVal, 10, 0)).getAsJsonArray();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }   
                        searchResults.getChildren().clear();
                        for(JsonElement element : json){
                                JsonObject obj = element.getAsJsonObject();
                                String resultTrack = obj.get("trk_name").getAsString();
                                String resultID = obj.get("trk_id").getAsString();
                                final String trackFinal = resultTrack.replaceFirst(".mp3","");
                                final String idFinal = resultID;
                                Button resultButton = new Button(trackFinal);
                                resultButton.setMaxWidth(Double.MAX_VALUE);
                                resultButton.setOnAction(e -> {
                                        songID = resultID;
                                        if(playing){
                                                plause.setText("\u23f5");
                                                playing = false;
                                                mediaPlayer.pause();
                                        }
                                        try{
                                                songPath = client.downloadSong(songID);
                                        } catch(Exception g){
                                                System.out.println("cant download");
                                        }
                                        media = new Media(songPath.toUri().toString());
                                        mediaPlayer = new MediaPlayer(media);
                                        plause.setText("\u23f8");
                                        playing = true;
                                        //songLength = getSongLength(metadata, songMetadata, client);
                                        new ProgressThread(progress/*, songElapsed, songLength*/).start();
                                        mediaPlayer.play();
                                        if (trackFinal.length() >= 20) {
                                                track.setText(trackFinal.substring(0, 15) + "...");
                                        } else {
                                                track.setText(trackFinal);
                                        }
                                        if (trackFinal.length() >= 35) {
                                                trackPlaying.setText(trackFinal.substring(0, 30) + "...");
                                        } else {
                                                trackPlaying.setText(trackFinal);
                                        }
                                });
                                resultButton.setOnMouseEntered(e -> {
                                        if(!meatballMenu.isShowing()){
                                                meatballMenu.show(resultButton, Side.BOTTOM, 0, 0);
                                        }
                                });
                                resultButton.setOnMouseExited(e ->{
                                        new Thread(() -> {
                                                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                                                if (!meatballMenu.isShowing()) return;
                                                javafx.application.Platform.runLater(() -> {
                                                    if (!meatballMenu.getOwnerNode().isHover() && !meatballMenu.isFocused())
                                                        meatballMenu.hide();
                                                });
                                        }).start();
                                });
                                meatballMenu.setOnHidden(e -> resultButton.disarm());
                                likeSong.setOnAction(e -> client.toggleSongLike(songID, username, password));
                                songToPlaylist.setOnAction(e -> {
                                        TextInputDialog playlistNamePopup = new TextInputDialog("sample_name");
                                        playlistNamePopup.setTitle("Playlist Name Request");
                                        playlistNamePopup.setHeaderText("Playlist you want this song added to: ");
                                        //playlistNamePopup.setContentText("Input:");
                                        Optional<String> result = playlistNamePopup.showAndWait();
                                        result.ifPresent(playlistName -> {
                                                System.out.println(client.addSongToPlaylist(songID, playlistName, username, password));
                                        });
                                });
                                searchResults.getChildren().add(resultButton);
                        }
                });
                //searchResults.getChildren().add(meatballMenu);
                root.setCenter(searchResults);
                homeButton.setOnAction(e -> root.setCenter(searchResults));


                // Profile Display
                VBox profileBox = new VBox(10);
                profileBox.setPadding(new Insets(10));
                profileBox.setStyle("-fx-border-color: black;");
                HBox profileBasics = new HBox();
                // insert profile picture
                Label profileName = new Label();
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

                // Settings Display
                VBox settingsBox = new VBox(10);
                settingsBox.setPadding(new Insets(10));
                settingsBox.setStyle("-fx-border-color: black;");
                Label colorTheme = new Label("Color Theme: ");
                colorTheme.setStyle("-fx-text-fill: silver");
                //System.out.println(getClass().getResource("resources/images/colorwheel.png"));
                //Image image = new Image(getClass().getResourceAsStream("/images/color_wheel.png"));
                Image colorWheelImage = null;
                try{colorWheelImage = new Image(new FileInputStream(colorWheelPath));}catch(Exception e){}
                ImageView colorWheel = new ImageView(colorWheelImage);
                colorWheel.setFitHeight(100);
                colorWheel.setFitWidth(100);
                settingsBox.getChildren().addAll(colorTheme, colorWheel);
                viewSettings.setOnAction(e -> root.setCenter(settingsBox));

                colorWheel.setOnMouseClicked(event -> {
                        double viewWidth = colorWheel.getBoundsInLocal().getWidth();
                        double viewHeight = colorWheel.getBoundsInLocal().getHeight();
                        double imageWidth = colorWheel.getImage().getWidth();
                        double imageHeight = colorWheel.getImage().getHeight();
                        double scaleX = imageWidth / viewWidth;
                        double scaleY = imageHeight / viewHeight;
                        int imgX = (int) (event.getX() * scaleX);
                        int imgY = (int) (event.getY() * scaleY);
                        PixelReader pixelReader = colorWheel.getImage().getPixelReader();
                        if (pixelReader != null) {
                            if (imgX >= 0 && imgX < colorWheel.getImage().getWidth() &&
                                imgY >= 0 && imgY < colorWheel.getImage().getHeight()) {
                                Color clickedColor = pixelReader.getColor(imgX, imgY);
                                if (clickedColor.getOpacity() > 0.1) {
                                        System.out.println("Picked color: " + clickedColor.toString());
                                        loginPage.setBackground(new Background(new BackgroundFill(
                                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                        new Stop(0, clickedColor),
                                                        new Stop(1, clickedColor.interpolate(javafx.scene.paint.Color.WHITE, 0.35))
                                                ),
                                                CornerRadii.EMPTY, Insets.EMPTY)));
                                        createAccountPage.setBackground(new Background(new BackgroundFill(
                                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                        new Stop(0, clickedColor),
                                                        new Stop(1, clickedColor.interpolate(javafx.scene.paint.Color.WHITE, 0.35))
                                                ),
                                                CornerRadii.EMPTY, Insets.EMPTY)));
                                        accountCreationSuccessPage.setBackground(new Background(new BackgroundFill(
                                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                        new Stop(0, clickedColor),
                                                        new Stop(1, clickedColor.interpolate(javafx.scene.paint.Color.WHITE, 0.35))
                                                ),
                                                CornerRadii.EMPTY, Insets.EMPTY)));
                                        root.setBackground(new Background(new BackgroundFill(
                                                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                                        new Stop(0, clickedColor),
                                                        new Stop(1, clickedColor.interpolate(javafx.scene.paint.Color.WHITE, 0.35))
                                                ),
                                                CornerRadii.EMPTY, Insets.EMPTY)));
                                        
                                }
                            }
                        }
                });

                // RIGHT
                VBox playingDetails = new VBox(10);
                playingDetails.setPadding(new Insets(10));
                playingDetails.setStyle("-fx-border-color: black;");
                ImageView albumcover = new ImageView(defaultMusicImage);
                albumcover.setFitHeight(100);
                albumcover.setFitWidth(100);
                if (currentSong.length() >= 20) {
                        track = new Label(currentSong.substring(0, 15) + "...");
                } else {
                        track = new Label(currentSong);
                }
                artist = new Label(currentArtist);
                track.setStyle("-fx-text-fill: silver");
                artist.setStyle("-fx-text-fill: silver");
                playingDetails.getChildren().addAll(albumcover, track, artist);
                root.setRight(playingDetails);

                // BOTTOM
                HBox bottom = new HBox(10);
                bottom.setPadding(new Insets(10));
                bottom.setMaxHeight(50);
                bottom.setStyle("-fx-border-color: black;");
                ImageView smallAlbumCover = new ImageView(new Image(defaultMusicImage));
                smallAlbumCover.setFitHeight(90);
                smallAlbumCover.setFitWidth(90);
                VBox trackDesc = new VBox(10);
                trackDesc.setPadding(new Insets(10));
                if (currentSong.length() >= 35) {
                        trackPlaying = new Label(currentSong.substring(0, 30) + "...");
                } else {
                        trackPlaying = new Label(currentSong);
                }
                artistPlaying = new Label(currentArtist);
                trackPlaying.setStyle("-fx-font-size: 10px; -fx-text-fill: silver");
                artistPlaying.setStyle("-fx-font-size: 10px; -fx-text-fill: silver");
                trackDesc.getChildren().addAll(trackPlaying, artistPlaying);
                trackDesc.setAlignment(Pos.CENTER);
                VBox playback = new VBox(10);
                playback.setPadding(new Insets(10));
                HBox controls = new HBox(10);
                controls.setPadding(new Insets(10));
                Button back = new Button("\u23ee");
                plause = new Button("\u23f5");
                Button forward = new Button("\u23ed");
                controls.getChildren().addAll(back, plause, forward);
                controls.setAlignment(Pos.CENTER);
                HBox trackProgress = new HBox(10);
                trackProgress.setPadding(new Insets(10));
                timeElapsed = new Label("0:00");
                timeElapsed.setStyle("-fx-text-fill: silver");
                progress = new ProgressBar();
                //new ProgressThread(progress, songElapsed, songLength).start();
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
                trackLength = new Label("0:00");
                trackLength.setStyle("-fx-text-fill: silver");
                trackProgress.getChildren().addAll(timeElapsed, progress, trackLength);
                trackProgress.setAlignment(Pos.CENTER);
                playback.getChildren().addAll(controls, trackProgress);
                bottom.getChildren().addAll(smallAlbumCover, trackDesc, playback);
                root.setBottom(bottom);

                try {
                        songPath = client.downloadSong(songID);
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                media = new Media(songPath.toUri().toString());
                mediaPlayer = new MediaPlayer(media);

                plause.setOnAction(e ->{
                        if(playing){
                                plause.setText("\u23f5");
                                playing = false;
                                mediaPlayer.pause();
                        }
                        else{
                                plause.setText("\u23f8");
                                playing = true;
                                //songLength = getSongLength(metadata, songMetadata, client);
                                new ProgressThread(progress/*, songElapsed, songLength*/).start();
                                mediaPlayer.play();
                        }
                });

                //SCENE SETTING
                applyWaveAnimation((Node)root);

                loginScene = new Scene(loginPage, 1000, 600);
                loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                mainScene = new Scene(root, 1000, 600);
                mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                createAccountScene = new Scene(createAccountPage, 1000, 600);
                createAccountScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                accountCreationSuccessScene = new Scene(accountCreationSuccessPage, 1000, 600);
                accountCreationSuccessScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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
                        if (client.authenticate(usernameInput.getText(), passwordInput.getText())){
                                // Now we know both credentials are correct, so the main page will now be displayed.
                                username = usernameInput.getText();
                                password = passwordInput.getText();
                                primaryStage.setScene(mainScene);
                                usernameInput.clear();
                                passwordInput.clear();
                        }else {
                                errorMsg.setText("ERROR: Username or password does not exist.");
                                errorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                                errorMsg.setFill(Color.RED);
                                return; // causes this program to fail to display the main page.
                        }
                    
                    /*try {
                        

                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }*/
                    profileButton.setText(username.charAt(0) + "");
                    profileName.setText(username);
                    primaryStage.setScene(mainScene);
                    usernameInput.clear();
                    passwordInput.clear();
                });
        }

        public static void main(String[] args) {
                launch(args);
        }
        
        private double getSongLength(String metadata, JsonArray songMetadata, MusicClient client){
                metadata = client.downloadSongData(songID, username, password);
                try {
                        songMetadata = JsonParser.parseString(metadata).getAsJsonArray();
                } catch (Exception g) {
                        g.printStackTrace();
                }  
                songLength = songMetadata.get(5).getAsJsonObject().get("trk_len").getAsDouble();
                return songLength;
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
                /*private double songElapsed;
                private final double songLength;*/

                public ProgressThread(ProgressBar progressBar/*, double songElapsed, double songLength*/) {
                        this.progressBar = progressBar;
                        /*this.songElapsed = songElapsed;
                        this.songLength = songLength;*/
                }

                @Override
                public void run() {
                        while(playing){
                                Platform.runLater(() -> progressBar.setProgress(
                                        mediaPlayer.getCurrentTime().toMillis()/mediaPlayer.getTotalDuration().toMillis())
                                        );
                                Platform.runLater(() -> timeElapsed.setText(
                                        ((int) mediaPlayer.getCurrentTime().toSeconds())/60 + 
                                        ":" + 
                                        String.format("%02d", ((int) mediaPlayer.getCurrentTime().toSeconds()) % 60)));
                                Platform.runLater(() -> trackLength.setText(
                                        ((int) mediaPlayer.getTotalDuration().toSeconds())/60 + 
                                        ":" + 
                                        String.format("%02d", ((int) mediaPlayer.getTotalDuration().toSeconds()) % 60)));
                                try {
                                        Thread.sleep(500);
                                        //songElapsed+=500;
                                } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                        }
                        System.out.println("terminating thread");
                }
        }
}
