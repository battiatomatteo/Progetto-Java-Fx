package utility;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.LogInView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UIUtils {

    //  mostra l'Alert
    public static void showAlert(Alert.AlertType t, String title, String txt) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }

    // metodo authenticate, in base al tipo 'flag' = 0 o 'flag' = 1 esegue un tipo di autenticazione
    public static boolean authenticate(String username, String password, int flag) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "";
        if(flag == 0 ){
            sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            sql = "SELECT * FROM utenti WHERE username = ?";
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static void LogOutButton(Stage stage){
        try {
            //Stage stage = (Stage) logOutButton.getScene().getWindow();
            // JFXPanel logOutButton = new JFXPanel();

            stage.close();
            new LogInView().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
