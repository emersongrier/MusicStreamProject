package desktopfrontend;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//import static com.BayWave.Tables.UserTable.passwordValid;
//import static com.BayWave.Tables.UserTable.usernameExists;
//import static java.sql.DriverManager.getConnection;

//import java.sql.SQLException;

public class DesktopFrontend extends Application {

        // private Stage window;
        private Scene mainScene, loginScene, createAccountScene, accountCreationSuccessScene;
        
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
                
                File imageFile = new File("C:\\Pictures\\Music Brand logo.jpg");
                Image img = new Image(imageFile.toURI().toString());
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
                
                Label existingUserLabel = new Label("Username: ");
                existingUserLabel.setStyle("-fx-text-fill: silver");
                TextField existingUsernameInput = new TextField();
                existingUsernameInput.setPromptText("Please enter your username here.");
                existingUsernameInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Label existingPasswordLabel = new Label("Password: ");
                existingPasswordLabel.setStyle("-fx-text-fill: silver");
                PasswordField existingPasswordInput = new PasswordField();
                existingPasswordInput.setPromptText("Please enter your password here.");
                existingPasswordInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Button signIn = new Button("Sign In");
                signIn.setAlignment(Pos.CENTER);
                
                Text loginErrorMsg = new Text();
                
                signIn.setOnAction(e -> {
                    if (existingUsernameInput.getText().isEmpty() || existingPasswordInput.getText().isEmpty()) {   
                        loginErrorMsg.setText("ERROR: Please enter both, your username and password.");
                        loginErrorMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
                        loginErrorMsg.setFill(Color.RED);
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
                    existingUsernameInput.clear();
                    existingPasswordInput.clear();
                });
                
                
                Hyperlink toCreateAccountPage = new Hyperlink("Please click here if you have not created an account.");
                toCreateAccountPage.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue");
                toCreateAccountPage.setOnAction(e -> {
                    primaryStage.setScene(createAccountScene);
                    existingUsernameInput.clear();
                    existingPasswordInput.clear();
                });
                loginPage.getChildren().addAll(iview, loginTitle, loginDesc, existingUserLabel, existingUsernameInput);
                loginPage.getChildren().addAll(existingPasswordLabel, existingPasswordInput, signIn, loginErrorMsg, toCreateAccountPage);

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
                
                Label newUserIDLabel = new Label("*UserID: ");
                newUserIDLabel.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                TextField newUserIDInput = new TextField();
                newUserIDInput.setPromptText("REQUIRED: Please enter your userID here.");
                newUserIDInput.setStyle("-fx-prompt-text-fill: purple;");
                
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
                
                // Assume the user's first name will be followed by last name; also middle initial is indeed OPTIONAL.
                Label newUserFullNameLabel = new Label("Full name: ");
                newUserFullNameLabel.setStyle("-fx-text-fill: silver");
                TextField newUserFullNameInput = new TextField();
                newUserFullNameInput.setPromptText("Please enter your full name (e.g. first-name, OPTIONAL: middle inital, last-name).");
                newUserFullNameInput.setStyle("-fx-prompt-text-fill: purple;");
                
                // Assume the user's phone number will be formatted in USA format, i.e. +1 (XXX)-XXX-XXXX.
                Label newPhoneLabel = new Label("Phone: ");
                newPhoneLabel.setStyle("-fx-text-fill: silver");
                TextField newPhoneInput = new TextField();
                newPhoneInput.setPromptText("Please enter your phone number, e.g. +1 (XXX)-XXX-XXXX.");
                newPhoneInput.setStyle("-fx-prompt-text-fill: purple;");
                
                Label birthdayLabel = new Label("Birthday: ");
                birthdayLabel.setStyle("-fx-text-fill: silver");
                DatePicker birthdateSelect = new DatePicker();
                birthdateSelect.setPrefWidth(225);
                birthdateSelect.setPromptText("Please select your birthdate.");
                birthdateSelect.getEditor().setStyle("-fx-prompt-text-fill: purple;");
                
                Label genderLabel = new Label("Gender: ");
                genderLabel.setStyle("-fx-text-fill: silver");
                RadioButton radioMale = new RadioButton("Male");
                RadioButton radioFemale = new RadioButton("Female");
                ToggleGroup genderGroup = new ToggleGroup();
                radioMale.setToggleGroup(genderGroup);
                radioMale.setStyle("-fx-text-fill: silver");
                radioFemale.setToggleGroup(genderGroup);
                radioFemale.setStyle("-fx-text-fill: silver");
                
                CheckBox checkOptIn = new CheckBox("Opt-in to receive newsletters,"
                + " promotional emails, or other personalized notifications.");
                checkOptIn.setStyle("-fx-text-fill: silver");
                
                CheckBox checkEULA = new CheckBox("*I agree to"
                + " accept the End User License Agreement and Privacy Policy.");
                checkEULA.setStyle("-fx-text-fill: red; -fx-font-weight: BOLD");
                        
                Button createAccount = new Button("Create Account");
                createAccount.setAlignment(Pos.CENTER);
                
                Text createAccountErrorMsg = new Text();
                
                createAccount.setOnAction(e -> {
                    if (newUserIDInput.getText().isEmpty() || newUsernameInput.getText().isEmpty() || 
                            (newPasswordInput.getText().isEmpty() || confirmPasswordInput.getText().isEmpty()) || 
                            newEmailInput.getText().isEmpty()) {
                        createAccountErrorMsg.setText("ERROR: Missing userID, username, email, or password.");
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
                loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                mainScene = new Scene(root, 600, 600);
                mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                createAccountScene = new Scene(createAccountPage, 1000, 1000);
                createAccountScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                accountCreationSuccessScene = new Scene(accountCreationSuccessPage, 700, 150);
                accountCreationSuccessScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                primaryStage.setScene(loginScene);
                primaryStage.setTitle("UI");
                primaryStage.show();

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
