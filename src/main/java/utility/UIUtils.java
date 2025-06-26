package utility;

import javafx.scene.control.Alert;

public class UIUtils {
    public static void showAlert(Alert.AlertType t, String title, String txt) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }
}
