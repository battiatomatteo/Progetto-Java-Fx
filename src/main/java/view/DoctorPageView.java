package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe view di DoctorPage
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/DoctorPage.fxml">DoctorPage.fxml</a>
 */
public class DoctorPageView extends Application {

    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/DoctorPage.fxml"));
        primaryStage.setTitle("Doctor Page");
        primaryStage.setScene(new Scene(root, 1150, 700));
        primaryStage.show();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}