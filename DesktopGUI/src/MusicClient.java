import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


public class MusicClient extends Application {

    @Override
    public void start(Stage stage) {
        TextField songInput = new TextField();
        songInput.setPromptText("Enter filename (e.g., test.mp3)");

        Button playButton = new Button("Play Song");

        playButton.setOnAction(e -> {
            String fileName = songInput.getText().trim();
            if (fileName.isEmpty()) return;

            String songUrl = "http://140.82.13.163:8080/song?file=" + fileName;
            try {
                // Download the file first
                URL url = new URL(songUrl);
                InputStream in = url.openStream();
                Path tempFile = Files.createTempFile("music_", "_" + fileName);
                Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Play from local file
                Media media = new Media(tempFile.toUri().toString());
                MediaPlayer player = new MediaPlayer(media);

                media.setOnError(() -> {
                    System.out.println("Media error: " + media.getError());
                });

                player.setOnError(() -> {
                    System.out.println("Player error: " + player.getError());
                });

                player.play();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        VBox root = new VBox(10, songInput, playButton);
        Scene scene = new Scene(root, 300, 150);
        stage.setTitle("Music Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
