package view;

import controllers.UserProfileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

/**
 * Classe view di PatientPane
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/UserProfile.fxml">UserProfile.fxml</a>
 */
public class UserProfileView extends Application {
    private final String username;

    /**
     * Setta lo username
     * @param username
     */
    public UserProfileView(String username) {
        this.username = username;
    }

    /**
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserProfile.fxml"));
        Parent root = loader.load();

        UserProfileController controller = loader.getController();
        controller.setProfiloUsername(username);

        Scene scene = new Scene(root, 960, 720);
        stage.setScene(scene);
        stage.setTitle("Profilo utente: " + username);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
