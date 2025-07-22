package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class patientPaneView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientPane.fxml"));
        primaryStage.setTitle("Patient Page");
        primaryStage.setScene(new Scene(root, 960, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
