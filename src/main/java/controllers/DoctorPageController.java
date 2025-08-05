package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import view.LogInView;
import java.io.IOException;

public class DoctorPageController {


    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> terapiaCol, farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private Button logOutButton;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private VBox patientsContainer;
    @FXML private Button addPatientButton, removeLastPatientButton;

    @FXML private StackPane notificationButton;
    @FXML private Label bellIcon;
    @FXML private Label notificationBadge;
    @FXML private VBox notificationPanel;

    private int notificheNonLette = 0;


    private final ObservableList<Terapia> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        //logOutButton.setOnAction(e -> LogOutButton());
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

        // Esempio: Aggiungiamo notifiche iniziali
        aggiungiNotifica("Terapia urgente per Mario");
        aggiungiNotifica("Controllo visita Rossi alle 14:00");

        // Clic sulla campanella per mostrare/nascondere
        notificationButton.setOnMouseClicked(this::toggleNotifiche);

        aggiungiPaziente();

    }

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

    private void aggiungiNotifica(String testo) {
        Label notifica = new Label("• " + testo);
        notificationPanel.getChildren().add(notifica);

        notificheNonLette++;
        notificationBadge.setText(String.valueOf(notificheNonLette));
        notificationBadge.setVisible(true);
    }

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



}