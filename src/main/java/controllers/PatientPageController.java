package controllers;

import javafx.animation.Animation;
import java.time.*;
import java.time.format.DateTimeFormatter;

import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import models.Pasto;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import view.LogInView;


public class PatientPageController {
    @FXML private TableView<Pasto> tableView;
    @FXML private TableColumn<Pasto, String> pastoColumn;
    @FXML private TableColumn<Pasto, String> preColumn;
    @FXML private TableColumn<Pasto, String> postColumn;
    @FXML private TableColumn<Pasto, String> orarioColumn;
    @FXML private Label messageStart;
    @FXML private Button logOutButton;

    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //messageStart.setText("Qui puoi inserire le somministrazioni giornaliere pre e post pasto .");

        pastoColumn.setCellValueFactory(cell -> cell.getValue().pastoProperty());
        orarioColumn.setCellValueFactory(cell -> cell.getValue().orarioProperty());
        preColumn.setCellValueFactory(cell -> cell.getValue().preProperty());
        postColumn.setCellValueFactory(cell -> cell.getValue().postProperty());

        pastoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orarioColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        postColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.setEditable(true);
        tableView.setItems(pastiData);

        // Dati iniziali
        pastiData.addAll(
                new Pasto("Colazione", "08:00", "", ""),
                new Pasto("Pranzo", "13:00", "", ""),
                new Pasto("Cena", "19:30", "", "")
        );

        avviaPromemoria();
        logOutButton.setOnAction(e -> LogOutButton());
    }

    private void avviaPromemoria() {
        Timeline timer = new Timeline(
                new KeyFrame(Duration.seconds(60), e -> {
                    String oraCorrente = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    for (Pasto p : pastiData) {
                        if (p.orarioProperty().get().equals(oraCorrente)) {
                            mostraAlert(p.getPasto());
                        }
                    }
                })
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void mostraAlert(String pasto) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Promemoria Pasto");
            alert.setHeaderText(null);
            alert.setContentText("È ora di somministrare per il pasto: " + pasto);
            alert.show();
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

    @FXML
    private void handleAddRow() {
        pastiData.add(new Pasto("Nuovo Pasto", "", "", ""));
    }
}
