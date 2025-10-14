package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class UserProfileView extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserProfile.fxml"));
        stage.setTitle("User Dashboard");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icona_dottore.jpg")));
        stage.setScene(new Scene(root, 900, 660));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
