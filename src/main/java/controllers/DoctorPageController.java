package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Terapia;
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

    private final ObservableList<Terapia> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        logOutButton.setOnAction(e -> LogOutButton());


        removeLastPatientButton.setOnAction(e -> {
            int count = patientsContainer.getChildren().size();
            if (count > 0) patientsContainer.getChildren().remove(count - 1);
        });

        addPatientButton.setOnAction(e -> aggiungiPaziente());
        removeLastPatientButton.setOnAction(e -> {
            int count = patientsContainer.getChildren().size();
            if (count > 0) patientsContainer.getChildren().remove(count - 1);
        });

    }

    private void LogOutButton(){
        try {
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.close();
            new LogInView().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void aggiungiPaziente() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientPane.fxml"));
            TitledPane pane = loader.load();
            pane.setText("Ricerca paziente " + (patientsContainer.getChildren().size() + 1));
            patientsContainer.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile caricare il pannello paziente.");
        }
    }


}