package controllers;

import DAO.UIUtilsDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utility.SessionManager;
import utility.UIUtils;
import view.AdminPageView;
import view.DoctorPageView;
import view.PatientPageView;

public class LogInController {

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private TextField visiblePassField;
    @FXML private ToggleButton showPasswordToggle;
    private static String user;
    private UIUtilsDao dao = new UIUtilsDao();

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

    public static String getUsername(){
        return user;
    }

    private void handleLogin() {
        String username = userField.getText();
        String password = passField.getText();
        user = username;
        if (UIUtils.authenticate(username, password, 0)) {
            messageLabel.setText("Accesso riuscito!");
            String tipo_utente = getTipoUtente(username);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            try {
                if (tipo_utente == null) {
                    messageLabel.setText("Tipo di utente non trovato.");
                } else {

                    SessionManager.currentUser = username;
                    SessionManager.currentRole = tipo_utente;

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

    private String getTipoUtente(String username) {
         return dao.tipoUtente(username);
    }

    private static String getErrore() {
        return "Errore di autenticazione. Riprova.";
    }
}