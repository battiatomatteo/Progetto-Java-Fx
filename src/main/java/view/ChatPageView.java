package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ChatPageView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/ChatPage.fxml"));
        // stage.setTitle("Chat con " + assignedDoctor);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icona_dottore.jpg")));
        stage.setScene(new Scene(root, 980, 720));
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
