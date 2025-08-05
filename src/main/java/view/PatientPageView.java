package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PatientPageView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientPage.fxml"));
        primaryStage.setTitle("Patient Page");
        primaryStage.setScene(new Scene(root, 980, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}