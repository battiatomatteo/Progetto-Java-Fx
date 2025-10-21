package view;

import controllers.DataUserController;
import controllers.UserProfileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe view di DataUser
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/DataUser.fxml">DataUser.fxml</a>
 */
public class DataUserView extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DataUser.fxml"));
        Parent root = loader.load();

        DataUserController controller = loader.getController();

        Scene scene = new Scene(root, 960, 720);
        stage.setScene(scene);
        stage.setTitle("Dati utente");
        stage.show();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}