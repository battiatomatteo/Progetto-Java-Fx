package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import view.AdminPageView;
import view.DoctorPageView;
import view.PatientPageView;

import java.sql.*;

public class LogInController {

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private TextField visiblePassField;
    @FXML private ToggleButton showPasswordToggle;


    // @FXML
    /*private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
    }*/

    private void handleLogin() {
        String username = userField.getText();
        String password = passField.getText();
        if (authenticate(username, password)) {
            messageLabel.setText("Accesso riuscito!");
            String tipo_utente = getTipoUtente(username);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            try {
                if (tipo_utente == null) {
                    messageLabel.setText("Tipo di utente non trovato.");
                } else {
                    if (tipo_utente.equals("paziente")) {
                        new PatientPageView().start(stage);
                    } else if (tipo_utente.equals("medico")) {
                        new DoctorPageView().start(stage);
                    } else {
                        new AdminPageView().start(stage);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            messageLabel.setText(getErrore());
        }
    }

    private static boolean authenticate(String username, String password) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";
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
    }

    private static String getTipoUtente(String username) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT tipo_utente FROM utenti WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo_utente");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getErrore() {
        return "Errore di autenticazione. Riprova.";
    }

    @FXML
    private void initialize() {
        visiblePassField.textProperty().bindBidirectional(passField.textProperty());

        showPasswordToggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            visiblePassField.setVisible(isSelected);
            visiblePassField.setManaged(isSelected);
            passField.setVisible(!isSelected);
            passField.setManaged(!isSelected);
        });

        loginButton.setOnAction(e -> handleLogin());
    }
}