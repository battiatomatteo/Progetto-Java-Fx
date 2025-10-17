package view;

import controllers.UserProfileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class UserProfileView extends Application {
    private final String username;

    public UserProfileView(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserProfile.fxml"));
        Parent root = loader.load();

        UserProfileController controller = loader.getController();
        controller.setProfiloUsername(username);

        Scene scene = new Scene(root, 960, 720);
        stage.setScene(scene);
        stage.setTitle("Profilo utente: " + username);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
