package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.Terapia;
import view.DoctorPageView;
import view.LogInView;

import java.sql.*;

public class DoctorPageController {

    @FXML private TextField usernameInput, newFarmacoInput, newAssunzioniInput, newQuantitaInput, newNoteInput;
    @FXML private Label labelUsername, label, label2;
    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> terapiaCol, farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private Button searchButton, addFarmacoButton, updateButton, deleteButton , logOutButton;
    @FXML private LineChart<String, Number> lineChart;

    private final ObservableList<Terapia> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        terapiaCol.setCellValueFactory(cellData -> cellData.getValue().idTerapiaProperty());
        farmacoCol.setCellValueFactory(cellData -> cellData.getValue().farmacoProperty());
        assunzioniCol.setCellValueFactory(cellData -> cellData.getValue().assunzioniProperty());
        quantFarCol.setCellValueFactory(cellData -> cellData.getValue().quantitaProperty());
        noteCol.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
        table.setItems(data);

        lineChart.setTitle("Andamento valori paziente");
        ((CategoryAxis)lineChart.getXAxis()).setLabel("Giorno");
        ((NumberAxis)lineChart.getYAxis()).setLabel("Valore");

        searchButton.setOnAction(e -> searchTerapie());
        addFarmacoButton.setOnAction(e -> aggiungiTerapia());
        deleteButton.setOnAction(e -> eliminaTerapia());
        logOutButton.setOnAction(e -> LogOutButton());
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

    private void searchTerapie() {
        String username = usernameInput.getText();
        data.clear();
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Inserire un utente prima di eseguire la ricerca.");
            return;
        }
        if (!authenticate(username)) {
            showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Paziente non trovato.");
            return;
        }
        // label che si possono vedere una volta che il paziente inserito viene trovato nel database
        // labelUsername.setText("Paziente trovato: " + username);
        label.setText("Lista delle terapie del paziente :");

        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT ID_terapia, farmaco, count_farmaco, quantità_farmaco, note FROM terapie WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                data.add(new Terapia(
                        rs.getString("ID_terapia"),
                        rs.getString("farmaco"),
                        rs.getString("count_farmaco"),
                        rs.getString("quantità_farmaco"),
                        rs.getString("note")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setItems(data);

        label2.setText("Grafico andamento terapia del paziente :");
        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valori");
        // Esempio dati fittizi
        series.getData().add(new XYChart.Data<>("Lun", 36.5));
        series.getData().add(new XYChart.Data<>("Mar", 37.0));
        series.getData().add(new XYChart.Data<>("Mer", 36.8));
        series.getData().add(new XYChart.Data<>("Gio", 37.2));
        series.getData().add(new XYChart.Data<>("Ven", 36.9));
        lineChart.getData().add(series);
    }

    private void aggiungiTerapia() {
        String username = usernameInput.getText();
        String farmaco = newFarmacoInput.getText();
        String assunzioni = newAssunzioniInput.getText();
        String quantita = newQuantitaInput.getText();
        String note = newNoteInput.getText();

        if (username.isEmpty() || farmaco.isEmpty() || assunzioni.isEmpty() || quantita.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila tutti i campi obbligatori!");
            return;
        }

        Alert riepilogo = new Alert(Alert.AlertType.CONFIRMATION);
        riepilogo.setTitle("Conferma inserimento terapia");
        riepilogo.setHeaderText("Riepilogo dati inseriti:");
        riepilogo.setContentText(
                "Username: " + username + "\n" +
                        "Farmaco: " + farmaco + "\n" +
                        "Assunzioni giornaliere: " + assunzioni + "\n" +
                        "Quantità (mg): " + quantita + "\n" +
                        "Note: " + note
        );
        if (riepilogo.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        String url = "jdbc:sqlite:miodatabase.db";
        int nextId = 1;
        String idSql = "SELECT MAX(ID_terapia) FROM terapie";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(idSql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String insertSql = "INSERT INTO terapie (ID_terapia, username, farmaco, count_farmaco, quantità_farmaco, note) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, username);
            pstmt.setString(3, farmaco);
            pstmt.setString(4, assunzioni);
            pstmt.setString(5, quantita);
            pstmt.setString(6, note);
            pstmt.executeUpdate();

            table.getItems().add(new Terapia(String.valueOf(nextId), farmaco, assunzioni, quantita, note));
            showAlert(Alert.AlertType.INFORMATION, "Terapia aggiunta", "Nuova terapia aggiunta con successo!");

            newFarmacoInput.clear();
            newAssunzioniInput.clear();
            newQuantitaInput.clear();
            newNoteInput.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void eliminaTerapia() {
        Terapia selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma eliminazione");
            confirm.setHeaderText(null);
            confirm.setContentText("Vuoi davvero eliminare la terapia selezionata?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                String idTerapia = selected.getIdTerapia();
                String sql = "DELETE FROM terapie WHERE id_terapia = ?";
                String url = "jdbc:sqlite:miodatabase.db";
                try (Connection conn = DriverManager.getConnection(url);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, idTerapia);
                    pstmt.executeUpdate();
                    table.getItems().remove(selected);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Seleziona una terapia da eliminare.");
        }
    }

    private boolean authenticate(String username) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT * FROM utenti WHERE username = ?";
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}