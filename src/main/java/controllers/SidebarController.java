package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class SidebarController {

    @FXML private VBox sidebarContainer;
    private boolean expanded = false;

    @FXML
    public void initialize() {
        sidebarContainer.setTranslateX(200);
        sidebarContainer.setVisible(true);
    }

    @FXML
    private void handleToggle() {
        expanded = !expanded;
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(expanded ? 0 : 200);
        transition.play();
    }
}
