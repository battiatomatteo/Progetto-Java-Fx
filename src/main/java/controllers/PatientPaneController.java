package controllers;

import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.ChartDataSetter;
import models.Terapia;
import utility.UIUtils;
import java.sql.*;
import javafx.scene.shape.Circle;


public class PatientPaneController {

    @FXML private ComboBox<StatoTerapia> statoComboBox;
    @FXML private TextField usernameInput, newFarmacoInput, newAssunzioniInput, newQuantitaInput, newNoteInput;
    @FXML private Label labelUsername, label, label2;
    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> terapiaCol, farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private TableColumn<Terapia, StatoTerapia> statoCol;
    @FXML private Button searchButton, addFarmacoButton, updateButton, deleteButton, generaPDF, filtraButton, salvaInfo;
    @FXML private VBox chartInclude;
    private final ObservableList<Terapia> data = FXCollections.observableArrayList();
    @FXML private PatientChartController chartIncludeController;
    @FXML private TextArea infoTextArea;

    @FXML
    private void initialize() {
        terapiaCol.setCellValueFactory(cell -> cell.getValue().idTerapiaProperty());
        statoCol.setCellValueFactory(cell -> cell.getValue().statoEnumProperty());
        farmacoCol.setCellValueFactory(cell -> cell.getValue().farmacoProperty());
        assunzioniCol.setCellValueFactory(cell -> cell.getValue().assunzioniProperty());
        quantFarCol.setCellValueFactory(cell -> cell.getValue().quantitaProperty());
        noteCol.setCellValueFactory(cell -> cell.getValue().noteProperty());
        table.setItems(data);

        statoComboBox.setItems(FXCollections.observableArrayList(StatoTerapia.values()));
        /*
        * assicura che il ComboBox mostri valori leggibili (e non OK, ATTESA, ERRORE tutti in maiuscolo)
        * e che non si verifichi più il ClassCastException
        */
        statoComboBox.setConverter(new StringConverter<StatoTerapia>() {
            @Override
            public String toString(StatoTerapia stato) {
                if (stato == null) return "";
                // Puoi anche personalizzare meglio: esempio -> "In Attesa"
                return switch (stato) {
                    case ATTIVA -> "Attiva";
                    case SOSPESA -> "Sospesa";
                    case TERMINATA -> "Terminata";
                };
            }

            @Override
            public StatoTerapia fromString(String string) {
                return switch (string.toLowerCase()) {
                    case "ok" -> StatoTerapia.ATTIVA;
                    case "attesa" -> StatoTerapia.SOSPESA;
                    case "errore" -> StatoTerapia.TERMINATA;
                    default -> null;
                };
            }
        });


        statoCol.setCellFactory(column -> new TableCell<>() {
            private final Circle circle = new Circle(6);

            protected void updateItem(StatoTerapia stato, boolean empty) {
                super.updateItem(stato, empty);
                if (empty || stato == null) {
                    setGraphic(null);
                } else {
                    switch (stato) {
                        case ATTIVA -> circle.setStyle("-fx-fill: green;");
                        case SOSPESA -> circle.setStyle("-fx-fill: orange;");
                        case TERMINATA -> circle.setStyle("-fx-fill: red;");
                    }
                    setGraphic(circle);
                }
            }
        });


        searchButton.setOnAction(e -> searchTerapie());
        addFarmacoButton.setOnAction(e -> aggiungiTerapia());
        deleteButton.setOnAction(e -> eliminaTerapia());
        updateButton.setOnAction(e -> updateTerapia());
        filtraButton.setOnAction(e -> UIUtils.filtraTerapia());
        generaPDF.setOnAction(e -> UIUtils.generaPDFReport( (Stage) generaPDF.getScene().getWindow(), usernameInput, chartInclude, table));
    }
    @FXML
    private void searchTerapie() {
        String username = usernameInput.getText();

        data.clear();
        if (username.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Inserire un utente prima di eseguire la ricerca.");
            return;
        }
        if (!UIUtils.authenticate(username, "", 1)) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Paziente non trovato.");
            return;
        }
        // label che si possono vedere una volta che il paziente inserito viene trovato nel database
        label.setText("Lista delle terapie del paziente :");

        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT ID_terapia, stato, farmaco, count_farmaco, quantità_farmaco, note FROM terapie WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                data.add(new Terapia(
                        rs.getString("ID_terapia"),
                        rs.getString("stato"),
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
        caricaInfoUtente(username);
        salvaInfo.setOnAction(e -> salvaModifiche(username));
        chartIncludeController.setData(new ChartDataSetter(username, ChartDataSetter.ALL)); // passo il nome del paziente
    }


    private void aggiungiTerapia() {
        String username = usernameInput.getText();
        String farmaco = newFarmacoInput.getText();
        String assunzioni = newAssunzioniInput.getText();
        String quantita = newQuantitaInput.getText();
        String note = newNoteInput.getText();
        //String stato = statoComboBox.getValue().toString();

        StatoTerapia statoEnum = statoComboBox.getValue();  // ComboBox<StatoTerapia>
        if (statoEnum == null) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Stato mancante", "Seleziona uno stato per la terapia!");
            return;
        }
        String stato = statoEnum.name(); // Ottieni "OK", "ATTESA", "ERRORE"

        if (username.isEmpty() || farmaco.isEmpty() || assunzioni.isEmpty() || quantita.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila tutti i campi obbligatori!");
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

        String insertSql = "INSERT INTO terapie (ID_terapia, username, farmaco, count_farmaco, quantità_farmaco, note, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, username);
            pstmt.setString(3, farmaco);
            pstmt.setString(4, assunzioni);
            pstmt.setString(5, quantita);
            pstmt.setString(6, note);
            pstmt.setString(7, stato);
            pstmt.executeUpdate();

            table.getItems().add(new Terapia(String.valueOf(nextId),stato, farmaco, assunzioni, quantita, note));
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Terapia aggiunta", "Nuova terapia aggiunta con successo!");

            resetCampi();
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
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Seleziona una terapia da eliminare.");
        }
    }

    public void updateTerapia(){

        String farmaco = newFarmacoInput.getText();
        String assunzioni = newAssunzioniInput.getText();
        String quantita = newQuantitaInput.getText();
        String note = newNoteInput.getText();
        //String stato = statoComboBox.getValue().toString();
        Terapia selected = table.getSelectionModel().getSelectedItem();
        // ottengo valore del ComboBox (se presente), altrimenti prendo lo stato esistente
        StatoTerapia statoEnum = statoComboBox.getValue();
        String stato = (statoEnum != null) ? statoEnum.name() : selected.getStatoEnum().name();

        if(farmaco.isEmpty() && assunzioni.isEmpty() && quantita.isEmpty() && note.isEmpty()){
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila almeno un campo per modificare");
            return;
        }
        try{
            //Terapia selected = table.getSelectionModel().getSelectedItem();  // spostata a riga 306
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Conferma modifiche");
                confirm.setHeaderText(null);
                confirm.setContentText("Vuoi davvero modificare la terapia selezionata?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                    String idTerapia = selected.getIdTerapia();

                    String sql = "UPDATE terapie SET farmaco = ?, count_farmaco = ?, quantità_farmaco = ?, note = ?, stato= ?  WHERE id_terapia = ?";
                    String url = "jdbc:sqlite:miodatabase.db";

                    try (Connection conn = DriverManager.getConnection(url);
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        if(farmaco.isEmpty()){
                            farmaco = selected.getFarmaco();
                        }
                        if(assunzioni.isEmpty()){
                            assunzioni = selected.getAssunzioni();
                        }
                        else {
                            try {
                                int n = Integer.parseInt(assunzioni);
                            }
                            catch ( NumberFormatException e) {
                                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore inserimento", "Hai inserito una lettera o simbolo al posto di un numero");
                                resetCampi();
                                return;
                            }

                        }
                        if(quantita.isEmpty()){
                            quantita = selected.getQuantita();
                        }else {
                            try {
                                double n = Double.parseDouble(quantita);
                            }
                            catch ( NumberFormatException e) {
                                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore inserimento", "Hai inserito una lettera o simbolo al posto di un numero");
                                resetCampi();
                                return;
                            }
                        }
                        if(note.isEmpty()){
                            note = selected.getNote();
                        }

                        pstmt.setString( 1, farmaco);
                        pstmt.setString(2, assunzioni);
                        pstmt.setString(3, quantita);
                        pstmt.setString(4, note);
                        pstmt.setString(5, stato);
                        pstmt.setString(6, idTerapia);
                        pstmt.executeUpdate();
                        table.getItems().remove(selected);
                        table.getItems().add(new Terapia(String.valueOf(idTerapia), stato, farmaco, assunzioni, quantita, note));
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Terapia aggiornata", " terapia aggiornata con successo!");
                        resetCampi();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            } else {
                UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Seleziona una terapia da modificare.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resetCampi(){
        newFarmacoInput.clear();
        newNoteInput.clear();
        newAssunzioniInput.clear();
        newQuantitaInput.clear();
    }


    public void caricaInfoUtente(String username) {
        String sql = "SELECT informazioni FROM utenti WHERE username = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:miodatabase.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                infoTextArea.setText(rs.getString("informazioni"));
            }

        } catch (SQLException e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore","Errore nel caricamento delle informazioni utente.");
            e.printStackTrace();
        }
    }

    private void salvaModifiche(String username) {
        String nuoveNote = infoTextArea.getText();

        String updateSql = "UPDATE utenti SET informazioni = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:miodatabase.db");
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, nuoveNote);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

            UIUtils.showAlert(Alert.AlertType.INFORMATION,"Successo", "Le informazioni utente sono state aggiornate.");

        } catch (SQLException ex) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio delle modifiche.");
            ex.printStackTrace();
        }
    }
}