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

/**
 * Controller della pagina LogIn.
 * @packege controllers
 * @see <a href="../resources/fxml/LogIn.fxml">LogIn.fxml</a>
 */
public class LogInController {

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private TextField visiblePassField;
    @FXML private ToggleButton showPasswordToggle;
    private UIUtilsDao dao = new UIUtilsDao();

    /**
     *  Questo metodo ha lo scopo di inizializzare.
     */
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

    /**
     * Questo metodo ha lo scopo di far accedere alla pagina corretta in base all'utente che esegue l'accesso, guardando il tipoUtente, se l'utente inserisce
     * delle credenziali errate, verrà visualizzato un label di errore.
     * @see view.PatientPageView
     * @see view.DoctorPageView
     * @see view.AdminPageView
     */
    private void handleLogin() {
        String username = userField.getText();
        String password = passField.getText();
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

    /**
     * Questo metodo ha lo scopo di prendere il tipo dell'utente che esegue l'accesso così da indirizzarlo nella pagina corretta nell'handleLogin().
     * @param username
     * @return stringa - tipo utente
     * @see DAO.UIUtilsDao
     */
    private String getTipoUtente(String username) {
         return dao.tipoUtente(username);
    }

    /**
     * Metodo con lo scopo di restituire una stringa di errore.
     * @return stringa - errore di autenticazione
     */
    private static String getErrore() {
        return "Errore di autenticazione. Riprova.";
    }
}