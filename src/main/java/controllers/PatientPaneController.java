package controllers;

import DAO.PatientPaneDao;
import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.ChartFilter;
import models.FilterDataSetter;
import models.Terapia;
import utility.SessionManager;
import utility.UIUtils;
import java.io.IOException;
import javafx.scene.shape.Circle;

/**
 * Controller del panello del paziente.
 * @packege controllers
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/PatientPane.fxml">PatientPane.fxml</a>
 */
public class PatientPaneController {

    @FXML private ComboBox<StatoTerapia> statoComboBox;
    @FXML private TextField usernameInput, newFarmacoInput, newAssunzioniInput, newQuantitaInput, newNoteInput;
    @FXML private Label  label, label2;
    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private TableColumn<Terapia, Integer > terapiaCol;
    @FXML private TableColumn<Terapia, StatoTerapia> statoCol;
    @FXML private Button searchButton, addFarmacoButton, updateButton, deleteButton, generaPDF, filtraButton, salvaInfo,chatButton;
    @FXML private VBox chartInclude;
    @FXML private PatientChartController chartIncludeController;
    @FXML private TextArea infoTextArea;
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientPaneDao
     */
    private PatientPaneDao dao;

    /**
     * Questo metodo ha lo scopo di inizializzare.
     * @see utility.UIUtils
     */
    @FXML
    private void initialize() {
        terapiaCol.setCellValueFactory(cell -> cell.getValue().idTerapiaProperty().asObject());
        statoCol.setCellValueFactory(cell -> cell.getValue().statoEnumProperty());
        farmacoCol.setCellValueFactory(cell -> cell.getValue().farmacoProperty());
        assunzioniCol.setCellValueFactory(cell -> cell.getValue().assunzioniProperty());
        quantFarCol.setCellValueFactory(cell -> cell.getValue().quantitaProperty());
        noteCol.setCellValueFactory(cell -> cell.getValue().noteProperty());
        dao = new PatientPaneDao();

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
        filtraButton.setOnAction(e -> apriFinestraScelta());
        generaPDF.setOnAction(e -> UIUtils.generaPDFReport( (Stage) generaPDF.getScene().getWindow(), usernameInput, chartInclude, table));
    }

    /**
     * Metodo che ha lo scopo di cercare le terapie presenti nel database del paziente cercato dal dottore.
     * Vengono lanciati degli Alert quando si presentano degli errori nella ricerca del paziente nel database
     * @see utility.UIUtils
     * @see DAO.PatientPaneDao
     */
    @FXML
    private void searchTerapie() {
        String username = usernameInput.getText();

        if (username.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Inserire un utente prima di eseguire la ricerca.");
            return;
        }
        int controlloAutenticazione = UIUtils.checkPatient(username, SessionManager.getCurrentUser());
        if (controlloAutenticazione == -1) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Paziente non trovato.");
            return;
        }
        else if(controlloAutenticazione == -2){
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore di ricerca", "Il medico attuale non può accedere alla cartella del paziente");
            return;
        }
        // label che si possono vedere una volta che il paziente inserito viene trovato nel database
        label.setText("Lista delle terapie del paziente :");
        FilterDataSetter filter = new FilterDataSetter(username,FilterDataSetter.ALL_STATUS_VIEWS,FilterDataSetter.ALL_THERAPY);
        table.setItems(dao.getTerapieList(filter));
        label2.setText("Grafico andamento terapia del paziente :");
        caricaInfoUtente(username);
        salvaInfo.setOnAction(e -> salvaModifiche(username));
        UIUtils.printMessage("inizializzazione in cerca");
        chartIncludeController.setData(username, new ChartFilter(ChartFilter.NO_START_DATE, ChartFilter.NO_END_DATE,ChartFilter.NO_ID )); // passo il nome del paziente
        chatButton.setVisible(true);
        filtraButton.setVisible(true);
    }

    /**
     * Questo metodo ha lo scopo di aggiungere una terapia al paziente, inserita dal medico.
     * @see utility.UIUtils
     * @see DAO.PatientPaneDao
     */
    private void aggiungiTerapia() {
        String username = usernameInput.getText();
        String farmaco = newFarmacoInput.getText();
        String assunzioni = newAssunzioniInput.getText();
        String quantita = newQuantitaInput.getText();
        String note = newNoteInput.getText();

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
        if(UIUtils.controlloParolaStringa(username) ||
                UIUtils.controlloParolaStringa(farmaco) ||
                UIUtils.controlloFloat(assunzioni) ||
                UIUtils.controlloFloat(quantita)){
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi Errati", "Compilare correttamente i campi inseriti");
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

    /**
     * Questo metodo ha lo scopo di eliminare una terapia al paziente, selezionata dalla tabella dal medico.
     * @see utility.UIUtils
     * @see DAO.PatientPaneDao
     */
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

    /**
     * Questo metodo ha lo scopo di aggiornare una terapia del paziente, selezionata dalla tabella dal medico.
     * @see utility.UIUtils
     * @see DAO.PatientPaneDao
     */
    public void updateTerapia(){
        String farmaco = newFarmacoInput.getText();
        String assunzioni = newAssunzioniInput.getText();
        String quantita = newQuantitaInput.getText();
        String note = newNoteInput.getText();
        Terapia selected = table.getSelectionModel().getSelectedItem();
        // ottengo valore del ComboBox (se presente), altrimenti prendo lo stato esistente
        StatoTerapia statoEnum = statoComboBox.getValue();

        if(farmaco.isEmpty() && assunzioni.isEmpty() && quantita.isEmpty() && note.isEmpty() && statoEnum == null){
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila almeno un campo per modificare");
            return;
        }
        String stato = statoEnum.name();
        try{
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
                        if(!UIUtils.controlloFloat(assunzioni)){
                            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore inserimento", "Hai inserito una lettera o simbolo al posto di un numero");
                            resetCampi();
                            return;
                        }
                    }
                    if(quantita.isEmpty()){
                        quantita = selected.getQuantita();
                    }else {
                        if(!UIUtils.controlloFloat(quantita)){
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

    /**
     * Questo metodo ha lo scopo di resettare i capi.
     */
    public void resetCampi(){
        newFarmacoInput.clear();
        newNoteInput.clear();
        newAssunzioniInput.clear();
        newQuantitaInput.clear();
    }

    /**
     * Questo metodo ha il compito di salvare le modifiche che si trovano nel box delle informazioni del paziente
     * @param username username del paziente
     * @see DAO.PatientPaneDao
     */
    private void salvaModifiche(String username){
        String nuoveNote = infoTextArea.getText();
        dao.updateInfoUtente(username, nuoveNote);
    }

    /**
     * Questo metodo ha il compito di caricare nel box le informazioni del paziente cercato.
     * @param username username del paziente
     * @see DAO.PatientPaneDao
     */
    public void caricaInfoUtente(String username){
        String result = dao.getInfoUtente(username);
        if(result == null){
            infoTextArea.setText("non ci sono informazioni");
        }
        else infoTextArea.setText(result);
    }

    /**
     * Questo metodo ha lo scopo di aprire la finestra della chat tra paziente e medico.
     * @throws IOException
     * @see utility.SessionManager
     * @see utility.UIUtils
     * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/ChatPage.fxml">ChatPage.fxml</a>
     * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/img/icona_dottore.jpg">icona_dottore.jpg</a>
     */
    @FXML
    private void openChat() throws IOException {
        // Verifico che l'utente sia loggato
        if (SessionManager.getCurrentUser() == null || SessionManager.getCurrentRole() == null) {
            System.out.println("Errore: utente non loggato o ruolo non definito.");
            return;
        }

        // Verifico che il ruolo sia effettivamente "doctor"
        if (!SessionManager.getCurrentRole().equals("medico")) {
            System.out.println("Accesso negato: solo i medici possono aprire questa chat.");
            return;
        }

        // determinare il paziente con cui il medico vuole chattare
        // Supponiamo che tu abbia una lista o tabella di pazienti da cui selezionare
        String selectedPatient = usernameInput.getText(); // TODO: prendo il nome utente selezionato dinamicamente

        // Carica la UI della chat
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatPage.fxml"));
        Parent root = loader.load();

        // Ottieni il controller della chat e inizializza la conversazione
        ChatController chatController = loader.getController();
        chatController.initializeChat(SessionManager.getCurrentUser(), selectedPatient);

        dao.cambioVisualizzato(SessionManager.getCurrentUser(), usernameInput.getText());

        // Crea una nuova finestra per la chat
        Stage stage = new Stage();
        stage.setTitle("Chat con " + selectedPatient);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icona_dottore.jpg")));
        stage.setScene(new Scene(root, 400, 350));
        stage.show();

    }

    /**
     * Questo metodo ha lo scopo di aprire la finestra per filtrare le ricerche nel pannello del paziente.
     * @see <a href="../resources/fxml/SceltaOpzione.fxml">SceltaOpzione.fxml</a>
     * @see SceltaOpzioneController
     */
    private void apriFinestraScelta() {
        String patientName = usernameInput.getText();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SceltaOpzione.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SceltaOpzioneController controller = loader.getController();
        controller.setUsername(patientName);
        controller.setWindowsData();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Blocco della finestra principale
        stage.setTitle("Seleziona un'opzione");
        stage.setScene(new Scene(root, 400, 400));
        stage.showAndWait(); // Attende la chiusura della finestra

        int stato = controller.getValoreStatoSelezionato();
        String farmaco = controller.getValoreFarmacoSelezionato();
        String dataInizio = controller.getDataInizio();
        String dataFine = controller.getDataFine();
        int seriesID = controller.getValoreSerieSelezionato();
        UIUtils.printMessage("valore stato selezionato patient pane   " + stato);
        UIUtils.printMessage("valore farmaco selezionato patient pane   " + farmaco);
        UIUtils.printMessage("valore dataIn selezionato patient pane   " + dataInizio);
        UIUtils.printMessage("valore dataFi selezionato patient pane   " + dataFine);

        FilterDataSetter filter = new FilterDataSetter(patientName, stato, farmaco);
        ChartFilter chartFilter = new ChartFilter(dataInizio, dataFine,seriesID );
        updatePaneData(filter,chartFilter);
    }

    /**
     * Questo metodo ha lo scopo di aggiornare il panello paziente in base all'utente cercato dal medico
     * @param filter    filtro per la tabella
     * @param chartFilter     filtro per il grafico
     * @see DAO.PatientPaneDao
     */
    private void updatePaneData(FilterDataSetter filter, ChartFilter chartFilter) {
        UIUtils.printMessage("updatePaneData");
        table.setItems(dao.getTerapieList(filter));
        chartIncludeController.setData(filter.getPatientUserName(), chartFilter); // passo il nome del paziente
    }

}