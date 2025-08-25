package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe view di PatientPane
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/PatientPage.fxml">PatientPage.fxml</a>
 */
public class PatientPageView extends Application {
    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientPage.fxml"));
        primaryStage.setTitle("Patient Page");
        primaryStage.setScene(new Scene(root, 980, 720));
        primaryStage.show();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}