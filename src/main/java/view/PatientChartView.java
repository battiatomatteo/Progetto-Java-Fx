package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe view di PatientChart
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/PatientChart.fxml">PatientChartr.fxml</a>
 */
public class PatientChartView extends Application {
    /**
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientChart.fxml"));
        primaryStage.setTitle("PatientChart Page");
        primaryStage.setScene(new Scene(root, 940, 700));
        primaryStage.show();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}