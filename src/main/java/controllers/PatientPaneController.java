package controllers;

import DAO.PatientPaneDao;
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
    @FXML private TableColumn<Terapia, String> farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private TableColumn<Terapia, Integer > terapiaCol;
    @FXML private TableColumn<Terapia, StatoTerapia> statoCol;
    @FXML private Button searchButton, addFarmacoButton, updateButton, deleteButton, generaPDF, filtraButton, salvaInfo;
    @FXML private VBox chartInclude;
   //private final ObservableList<Terapia> data = FXCollections.observableArrayList();
    @FXML private PatientChartController chartIncludeController;
    @FXML private TextArea infoTextArea;
    private PatientPaneDao dao;

    @FXML
    private void initialize() {
        terapiaCol.setCellValueFactory(cell -> cell.getValue().idTerapiaProperty().asObject());
        statoCol.setCellValueFactory(cell -> cell.getValue().statoEnumProperty());
        farmacoCol.setCellValueFactory(cell -> cell.getValue().farmacoProperty());
        assunzioniCol.setCellValueFactory(cell -> cell.getValue().assunzioniProperty());
        quantFarCol.setCellValueFactory(cell -> cell.getValue().quantitaProperty());
        noteCol.setCellValueFactory(cell -> cell.getValue().noteProperty());
        dao = new PatientPaneDao();
        //table.setItems(data);

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
        filtraButton.setOnAction(e -> UIUtils.filtraTerapia(usernameInput.getText()));
        generaPDF.setOnAction(e -> UIUtils.generaPDFReport( (Stage) generaPDF.getScene().getWindow(), usernameInput, chartInclude, table));
    }
    @FXML
    private void searchTerapie() {
        String username = usernameInput.getText();

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

        table.setItems(dao.getTerapieList(username));
        //table.setItems(data);

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

        table.getItems().add(dao.addNewTerapia(username, stato, farmaco, assunzioni, quantita, note));
        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Terapia aggiunta", "Nuova terapia aggiunta con successo!");
        resetCampi();
    }

    private void eliminaTerapia() {
        Terapia selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma eliminazione");
            confirm.setHeaderText(null);
            confirm.setContentText("Vuoi davvero eliminare la terapia selezionata?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                int idTerapia = selected.getIdTerapia();
                dao.removeTerapia(idTerapia);
                table.getItems().remove(selected);
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
        Terapia selected = table.getSelectionModel().getSelectedItem();
        // ottengo valore del ComboBox (se presente), altrimenti prendo lo stato esistente
        StatoTerapia statoEnum = statoComboBox.getValue();
        //String stato = (statoEnum != null) ? statoEnum.name() : selected.getStatoEnum().name();

        if(farmaco.isEmpty() && assunzioni.isEmpty() && quantita.isEmpty() && note.isEmpty() && statoEnum == null){
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila almeno un campo per modificare");
            return;
        }
        String stato = statoEnum.name();
        try{
            //Terapia selected = table.getSelectionModel().getSelectedItem();  // spostata a riga 306
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Conferma modifiche");
                confirm.setHeaderText(null);
                confirm.setContentText("Vuoi davvero modificare la terapia selezionata?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                    int idTerapia = selected.getIdTerapia();

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

                    table.getItems().remove(selected);
                    table.getItems().add(dao.updateTerapia(idTerapia, stato, farmaco, assunzioni, quantita, note));
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Terapia aggiornata", " terapia aggiornata con successo!");
                    resetCampi();
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

    private void salvaModifiche(String username){
        String nuoveNote = infoTextArea.getText();
        dao.updateInfoUtente(username, nuoveNote);
    }

    public void caricaInfoUtente(String username){
        String result = dao.getInfoUtente(username);
        if(result == null){
            infoTextArea.setText("non ci sono informazioni");
        }
        else infoTextArea.setText(result);
    }



}