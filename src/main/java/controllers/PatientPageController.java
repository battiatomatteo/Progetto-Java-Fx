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
import utility.UIUtils;
import view.LogInView;


public class PatientPageController {
    @FXML private TableView<Pasto> tableView;
    @FXML private TableColumn<Pasto, String> pastoColumn;
    @FXML private TableColumn<Pasto, String> preColumn;
    @FXML private TableColumn<Pasto, String> postColumn;
    @FXML private TableColumn<Pasto, String> orarioColumn;
    @FXML private Label messageStart;
    @FXML private Button logOutButton, nuovaSomministrazioneButton;

    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //messageStart.setText("Qui puoi inserire le somministrazioni giornaliere pre e post pasto .");
        tableView.setEditable(true);

        // Imposta le proprietà dei dati
        pastoColumn.setCellValueFactory(cellData -> cellData.getValue().pastoProperty());
        orarioColumn.setCellValueFactory(cellData -> cellData.getValue().orarioProperty());
        preColumn.setCellValueFactory(cellData -> cellData.getValue().preProperty());
        postColumn.setCellValueFactory(cellData -> cellData.getValue().postProperty());

        // Rendi le celle editabili
        pastoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orarioColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        postColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Salva le modifiche nel modello
        pastoColumn.setOnEditCommit(event -> {
            Pasto p = event.getRowValue();
            p.setPasto(event.getNewValue());
        });

        orarioColumn.setOnEditCommit(event -> {
            Pasto p = event.getRowValue();
            p.setOrario(event.getNewValue());
        });

        preColumn.setOnEditCommit(event -> {
            Pasto p = event.getRowValue();
            p.setPre(event.getNewValue());
        });

        postColumn.setOnEditCommit(event -> {
            Pasto p = event.getRowValue();
            p.setPost(event.getNewValue());
        });

        tableView.setItems(pastiData);

        // Dati iniziali
        pastiData.addAll(
                new Pasto("Colazione", "08:00", "", ""),
                new Pasto("Pranzo", "13:00", "", ""),
                new Pasto("Cena", "19:30", "", "")
        );
        nuovaSomministrazioneButton.setOnAction(e -> nuovaSomministrazione());
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

    private void nuovaSomministrazione(){
        try{
            stampaTabella();
            StringBuilder riepilogo = new StringBuilder("Riepilogo somministrazione:\n");

            for (Pasto p : tableView.getItems()) {
                riepilogo.append("🍽 ")
                        .append(p.getPasto())
                        .append(" (").append(p.getOrario()).append("): ")
                        .append("Pre = ").append(p.getPre()).append(", ")
                        .append("Post = ").append(p.getPost()).append("\n");
            }

            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Somministrazione salvata", riepilogo.toString());

            // Qui potresti anche chiamare il metodo DAO per salvare nel DB
            // esempio: pastoDAO.updatePasto(p);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void stampaTabella() {
        System.out.println("===== CONTENUTO TABELLA =====");
        for (Pasto p : tableView.getItems()) {
            System.out.println(
                    "Pasto: " + p.getPasto() +
                    " | Orario: " + p.getOrario() +
                    " | Pre Pasto: " + p.getPre() +
                    " | Post Pasto: " + p.getPost());
        }
        System.out.println("=============================");
    }

}
