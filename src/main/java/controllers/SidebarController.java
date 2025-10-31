package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * Controller della finestra che permette di rendere la sidebar visibile.
 *
 * @package controllers
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/sidebar.fxml">sidebar.fxml</a>
 */
public class SidebarController {

    // Attributi della classe
    @FXML private VBox sidebarContainer;
    private boolean expanded = false;

    /**
     * Metodo di inizializzazione automatico eseguito al caricamento del controller.
     * Imposta la sidebar nel corretto modo.
     */
    @FXML
    public void initialize() {
        sidebarContainer.setTranslateX(200);
        sidebarContainer.setVisible(true);
    }

    /**
     *
     */
    @FXML
    private void handleToggle() {
        expanded = !expanded;
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(expanded ? 0 : 200);
        transition.play();
    }
}
