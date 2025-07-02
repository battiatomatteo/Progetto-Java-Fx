package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PatientChartView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientChart.fxml"));
        primaryStage.setTitle("PatientChart Page");
        primaryStage.setScene(new Scene(root, 940, 700));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}