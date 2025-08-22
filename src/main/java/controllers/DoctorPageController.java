package controllers;

import DAO.DoctorPageDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Terapia;
import utility.SessionManager;
import utility.UIUtils;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller della pagina LogIn.
 * @packege controllers
 * @see <a href="../resources/fxml/DoctorPage.fxml">DoctorPage.fxml</a>
 */
public class DoctorPageController {
    @FXML private Button logOutButton;
    @FXML private VBox patientsContainer;
    @FXML private Button addPatientButton, removeLastPatientButton, buttonMessAll;
    @FXML private TextArea areaMessAll;
    @FXML private StackPane notificationButton;
    @FXML private Label notificationBadge;
    @FXML private VBox notificationPanel;
    private DoctorPageDao dao = new DoctorPageDao();
    private int notificheNonLette = 0;

    /**
     * Questo metodo ha lo scopo di inizializzare.
     * @see utility.UIUtils
     * @see utility.SessionManager
     */
    @FXML
    private void initialize() {

        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));


        removeLastPatientButton.setOnAction(e -> {
            int count = patientsContainer.getChildren().size();
            if (count > 0) patientsContainer.getChildren().remove(count - 1);
        });

        addPatientButton.setOnAction(e -> aggiungiPaziente());
        removeLastPatientButton.setOnAction(e -> {
            int count = patientsContainer.getChildren().size();
            if (count > 0) patientsContainer.getChildren().remove(count - 1);
        });

        recuperoNotifiche();

        // Clic sulla campanella per mostrare/nascondere
        notificationButton.setOnMouseClicked(this::toggleNotifiche);

        aggiungiPaziente();
        buttonMessAll.setOnAction(e -> sendAllMess(SessionManager.currentUser));

    }

    /**
     * Questo metodo ha lo scopo di aggiungere un pannello di ricerca paziente, ogni pannello ha lo scopo di mostrare la tabella clinica id un paziente
     * e di prescrivere nuove terapie.
     * Lancia un Alert nel caso sia impossibile caricare un nuovo panello.
     * @see <a href="../resources/fxml/PatientPane.fxml">PatientPane.fxml</a>
     */
    private void aggiungiPaziente() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientPane.fxml"));
            TitledPane pane = loader.load();
            pane.setText("Ricerca paziente " + (patientsContainer.getChildren().size() + 1));
            pane.setText("Ricerca paziente ");
            patientsContainer.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile caricare il pannello paziente.");
        }
    }

    /**
     * Questo metodo ha lo scopo di aggiungere una notifica nel box notifiche del dottore, nel caso ce ne fosse almeno una.
     * @param testo testo della notifica
     */
    private void aggiungiNotifica(String testo) {
        Label notifica = new Label("• " + testo);
        notificationPanel.getChildren().add(notifica);

        notificheNonLette++;
        notificationBadge.setText(String.valueOf(notificheNonLette));
        notificationBadge.setVisible(true);
    }

    /**
     * Questo metodo ha lo scopo di mostrare il numero di notifiche al medico.
     * @param event
     */
    private void toggleNotifiche(MouseEvent event) {
        boolean isVisible = notificationPanel.isVisible();
        notificationPanel.setVisible(!isVisible);
        notificationPanel.setManaged(!isVisible); // importante per gestire layout

        if (!isVisible) {
            // Se l'utente apre il pannello, azzeriamo il contatore
            notificheNonLette = 0;
            notificationBadge.setVisible(false);
        }
    }

    /**
     * Questo metodo ha lo scopo di mandare il messaggio a tutti i pazienti del dottore loggato
     * @param username
     * @see DAO.DoctorPageDao
     */
    private void sendAllMess(String username){
        dao.sendAllMess(username, areaMessAll.getText());
    }

    /**
     * Questo metodo ha lo scopo di recuperare le notifiche da mostrare al medico , se ha nnuovi messaggi non letti.
     * @see DAO.DoctorPageDao
     * @see utility.SessionManager
     */
    private void recuperoNotifiche(){
        ArrayList<String> listaNotifiche = dao.recuperoNotifica(SessionManager.currentUser);
        listaNotifiche.forEach(this::aggiungiNotifica);
    }

}