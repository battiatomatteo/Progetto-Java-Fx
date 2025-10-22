package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe view di LogIn
 * @PAckege view
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/LogIn.fxml">LogIn.fxml</a>
 */
public class LogInView extends Application {
    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LogIn.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icona_dottore.jpg")));
        primaryStage.setScene(new Scene(root, 350, 200));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}