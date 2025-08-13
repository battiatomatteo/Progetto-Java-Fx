package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import DAO.PatientPaneDao;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.FloatStringConverter;


import DAO.PatientPageDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.*;
import utility.SessionManager;
import utility.UIUtils;
import java.time.LocalDate;
import java.util.*;


public class PatientPageController {
    @FXML private Label notificationBadge;
    @FXML private TableView<Pasto> tableView;
    @FXML private TableColumn<Pasto, String> pastoColumn;
    @FXML private TableColumn<Pasto, Float> preColumn;
    @FXML private TableColumn<Pasto, Float> postColumn;
    @FXML private TableColumn<Pasto, String> orarioColumn;
    @FXML private Label messageStart, infoPaziente;
    @FXML private Button logOutButton, nuovaSomministrazioneButton, salvaSintomi;
    @FXML private TextArea textArea;
    @FXML private VBox lineChart;
    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();
    @FXML private PatientChartController chartIncludeController;
    private PatientPageDao dao;
    private PatientPaneDao dao2;

    private boolean hasNotification = false;

    private static final int PREPASTOMIN = 80;
    private static final int PREPASTOMAX = 130;
    private static final int POSTPASTOMAX = 180;

    @FXML
    private void initialize() {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        dao = new PatientPageDao();
        dao2 = new PatientPaneDao();

        messageStart.setText("Qui puoi inserire le somministrazioni giornaliere pre e post pasto di  " + UIUtils.dataConGiorno()  );
        // Imposta le proprietà dei dati
        pastoColumn.setCellValueFactory(cellData -> cellData.getValue().pastoProperty());
        orarioColumn.setCellValueFactory(cellData -> cellData.getValue().orarioProperty());
        preColumn.setCellValueFactory(cellData -> cellData.getValue().preProperty().asObject());
        postColumn.setCellValueFactory(cellData -> cellData.getValue().postProperty().asObject());

        // Rendi le celle editabili
        pastoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        orarioColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        postColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));

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

        nuovaSomministrazioneButton.setOnAction(e -> nuovaSomministrazione());
        caricaSomministrazioniOdierne(SessionManager.currentUser);
        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));
        salvaSintomi.setOnAction(e -> salvaSintomibox(textArea.getText()));
        caricaInfoPaziente(SessionManager.currentUser);
        UIUtils.printMessage("inizializzazione grafico paziente");
        chartIncludeController.setData(LogInController.getUsername(), new ChartFilter(ChartFilter.NO_START_DATE, ChartFilter.NO_END_DATE,ChartFilter.NO_ID ) ); // passo il nome del paziente

        recuperoNotifiche();

        Platform.runLater(() -> checkSommOdierne());
    }

    @FXML
    private void openChat() throws IOException {
        // Verifico che l'utente sia loggato
        if (SessionManager.currentUser == null || SessionManager.currentRole == null) {
            System.out.println("Errore: utente non loggato o ruolo non definito.");
            return;
        }

        // Verifico che il ruolo sia effettivamente "patient"
        if (!SessionManager.currentRole.equals("paziente")) {
            System.out.println("Accesso negato: solo i pazienti possono aprire questa chat.");
            return;
        }

        // Determina il medico con cui il paziente deve chattare
        String assignedDoctor = UIUtils.getDoctor(SessionManager.currentUser); // TODO: prendo dinamicamente il medico assegnato

        toggleNotifiche();

        // Carica l'interfaccia della chat
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatPage.fxml"));
        Parent root = loader.load();

        // Ottieni il controller della chat e inizializza la conversazione
        ChatController chatController = loader.getController();
        chatController.initializeChat(SessionManager.currentUser, assignedDoctor);

        // Crea una nuova finestra per la chat
        Stage stage = new Stage();
        stage.setTitle("Chat con " + assignedDoctor);
        stage.setScene(new Scene(root, 400, 350));
        stage.show();
    }

    private void aggiungiNotifica() {
        notificationBadge.setText(String.valueOf(1));
        notificationBadge.setVisible(true);
    }

    private void toggleNotifiche() {
        if (hasNotification) {
            // Se l'utente apre la chat, azzeriamo il contatore
            dao.cambioVisualizzato(UIUtils.getDoctor(SessionManager.currentUser), SessionManager.currentUser);
            notificationBadge.setVisible(false);
            hasNotification = false;
        }
    }

    private void recuperoNotifiche(){
        hasNotification = dao.recuperoNotifica(SessionManager.currentUser);
        if(hasNotification) aggiungiNotifica();
    }

    /*
     * Cosa fa nuovaSomministrazione() :
     * Controlla per ogni pasto se esiste già un record con quella data e orario, Se esiste: non lo reinserisce.
     * Se non esiste e pre/post sono validi, lo inserisce.
     * Mostra un riepilogo solo dei pasti inseriti.
     * */
    private void nuovaSomministrazione() {
        LocalDate oggi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder riepilogo = new StringBuilder("Riepilogo somministrazione:\n");
        Day rilevazioneGiorno = new Day(oggi);

        for (Pasto p : tableView.getItems()){
            String orario = p.getOrario();
            if (!dao.checkSomministarazione(orario, oggi, formatter, SessionManager.currentUser))
                continue; // Esiste già, quindi salto
            float pre = p.getPre();
            float post = p.getPost();

            // controllo valori somministrazione
            if((pre < PREPASTOMIN || pre > PREPASTOMAX || post > POSTPASTOMAX ) && (pre != 0 && post != 0)) {
                UIUtils.showAlert(Alert.AlertType.WARNING, "Valori somministrazione", "I valori di questa somministrazione sono fuori dal range : ");
                // mess di def. somm
                dao.messageSomm(autoNotificationContent(pre, post , orario, p.getPasto(), rilevazioneGiorno.getDataString()), UIUtils.getDoctor(SessionManager.currentUser), SessionManager.currentUser);
            }
            rilevazioneGiorno.addPasto(new Pasto(null,orario,pre,post));

            riepilogo.append("🍽 ")
                    .append(p.getPasto())
                    .append(" (").append(orario).append("): ")
                    .append("Pre = ").append(pre).append(", ")
                    .append("Post = ").append(post).append("\n");
        }

        if (dao.addSomministrazione(rilevazioneGiorno, SessionManager.currentUser)) {
            stampaTabella();
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Somministrazione salvata", riepilogo.toString());
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessun pasto inserito", "Tutti i pasti erano già presenti o non validi (pre/post nulli o 0).");
        }
    }

    private String autoNotificationContent(float pre , float post, String orario, String pasto, String data){
        return "Messaggio somministrazione : oggi " + data + " , " + pasto + " ore "+ orario +" pre : " +
                pre + " e post "+ post + " i valore/i sono fuori dal range consentito.";
    }

    private void caricaSomministrazioniOdierne(String username) {
        pastiData.clear(); // Pulisce la tabella

        Map<String, Pasto> sommRilevati = new HashMap<>();
        sommRilevati.putAll(dao.somministrazioneTabella(username));

        // Definisci gli orari attesi
        Map<String, String> orariPrevisti = new LinkedHashMap<>();  // new LinkedHashMap<>(): simile as HashMap ma mantiene l'ordine di inserimento degli elementi
        orariPrevisti.put("08:00", "Colazione");
        orariPrevisti.put("13:00", "Pranzo");
        orariPrevisti.put("19:30", "Cena");

        // Aggiunge i pasti ordinati (colazione, pranzo, cena)
        for (Map.Entry<String, String> entry : orariPrevisti.entrySet()) {
            String orario = entry.getKey();
            String nome = entry.getValue();

            if (sommRilevati != null && sommRilevati.containsKey(orario)) {
                pastiData.add(sommRilevati.get(orario));
            } else {
                // caso in cui non ho somministrazioni in questo giorno: sommRilevati != null
                pastiData.add(new Pasto(nome, orario, 0, 0));
            }
        }
    }

    /*
     * Cosa fa salvaSintomibox(String nuovaNota) :
     * salvo la nota nell'ultima somministrazione inserita nel giorno, nel caso in cui non si ha nessuna somministrazione oggi
     * controllo se l'ultima somministrazione del giorno precedente ha una nota != "note..." , se true la sovrascrivo,
     * se false mando un alert dicendo di contattare il medico o di aspettare di inserire una somministrazione
     * */
    private void salvaSintomibox(String nuovaNota){
        // salvo nel database (in "note_rivelazione") ciò che l'utente scrive nel box sintomi che poi verrà mostrato al medico
        String url = "jdbc:sqlite:miodatabase.db";
        LocalDate oggi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (Connection conn = DriverManager.getConnection(url)){

            // 1. Controllo se ci sono somministrazioni oggi
            String queryOggi = "SELECT ID_rilevazioni FROM rilevazioni_giornaliere WHERE data_rilevazione = ? ORDER BY ID_rilevazioni DESC LIMIT 1";
            try (PreparedStatement pstmtOggi = conn.prepareStatement(queryOggi)) {
                pstmtOggi.setString(1, oggi.format(formatter));
                ResultSet rsOggi = pstmtOggi.executeQuery();

                if (rsOggi.next()) {
                    // Somministrazione trovata per oggi → aggiorna la più recente
                    int id = rsOggi.getInt("ID_rilevazioni");
                    String update = "UPDATE rilevazioni_giornaliere SET note_rilevazione = ? WHERE ID_rilevazioni = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                        updateStmt.setString(1, nuovaNota);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Nota salvata", "Nota salvata sulla somministrazione odierna.");
                        return;
                    }
                }
            }

            // 2. Nessuna somministrazione oggi → cerco l’ultima disponibile
            String queryUltima = "SELECT ID_rilevazioni, note_rilevazione, data_rilevazione FROM rilevazioni_giornaliere ORDER BY data_rilevazione DESC, ID_rilevazioni DESC LIMIT 1";
            try (PreparedStatement pstmtUltima = conn.prepareStatement(queryUltima);
                 ResultSet rsUltima = pstmtUltima.executeQuery()) {

                if (rsUltima.next()) {
                    String note = rsUltima.getString("note_rilevazione");
                    int id = rsUltima.getInt("ID_rilevazioni");
                    String dataUltima = rsUltima.getString("data_rilevazione");

                    if ("note...".equalsIgnoreCase(note)) {
                        // Aggiorno la nota
                        String update = "UPDATE rilevazioni_giornaliere SET note_rilevazione = ? WHERE ID_rilevazioni = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                            updateStmt.setString(1, nuovaNota);
                            updateStmt.setInt(2, id);
                            updateStmt.executeUpdate();
                            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Nota salvata", "Nota salvata nella somministrazione più recente del " + dataUltima);
                        }
                    } else {
                        // Nota già presente → mostro alert
                        UIUtils.showAlert(Alert.AlertType.WARNING, "Nota non salvata", "Hai già scritto una nota nella somministrazione più recente. Contatta il medico o attendi una nuova somministrazione.");
                    }
                } else {
                    UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna rilevazione", "Non è presente alcuna somministrazione su cui salvare la nota.");
                }
            }
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio della nota: " + e.getMessage());
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

    private void caricaInfoPaziente(String username){
        infoPaziente.setText(dao2.getInfoUtente(username));
    }


    /*
     * controllo se l'utente non ha inserito una o più somministrazioni oggi
     */
    private void checkSommOdierne() {
        LocalTime ora = LocalTime.now();

        int oraAttuale = ora.getHour(); // restituisce solo l'ora (0-23)

        // System.out.println("Ora intera: " + oraAttuale);

        final String[] s = {""};
        tableView.getItems().forEach((Pasto p) -> {
            //UIUtils.printMessage("--foreach controllo" + p + "\n valore di s " + s[0]);
            if (!controllo(p , oraAttuale)){
                // System.out.println(" sono nell'if in check");
                s[0] +="\n  - " + p.getPasto();
                // System.out.println(" messaggio attuale " + s[0]);
            }
        });
        if(! s[0].isEmpty()) {
            // warning all'utente
            UIUtils.showAlert(Alert.AlertType.WARNING, "Attenzione", "Mancano le rilevazioni delle ore precedenti \n"+ s[0]);
            // messaggio al medico
            // data2 è la data di oggi e data1 è la data di 3 giorni prima di data2 , controllo se data1 è nel db
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate oggi = LocalDate.now(); // Ottieni la data corrente
            String data2 = oggi.format(formatter); // Converte la data in stringa formattata

            // 1. Converte la stringa in LocalDate
            LocalDate data = LocalDate.parse(data2, formatter);

            // 2. Sottrae 3 giorni
            LocalDate nuovaData = data.minusDays(3);

            // 3. Converte di nuovo in stringa
            String data1 = nuovaData.format(formatter);

            ChartFilter filter = new ChartFilter(data1, data2, ChartFilter.NO_ID);
            if(dao.messageSommDim(data1, data2 , filter, SessionManager.currentUser)) {
                // content mess al dottore
                String content = "Mancano delle somministrazioni da parte di " + SessionManager.currentUser+ " da almeno 3 giorni . Oggi è il : " + data2;
                // invio mess al dottore
                if(! dao.messDuplicato(content, UIUtils.getDoctor(SessionManager.currentUser), SessionManager.currentUser)) {
                    dao.messageSomm(content, UIUtils.getDoctor(SessionManager.currentUser), SessionManager.currentUser);
                }
            }
            else
                System.out.println("Non bisogna mandare un mess al dottore");
        }
    }

    private boolean controllo(Pasto pasto, int oraAttuale) {
        LocalTime time = LocalTime.parse(pasto.getOrario(),DateTimeFormatter.ofPattern("HH:mm") );
        int oraInt = time.getHour();
        // System.out.println("valori pasti: " + pasto.getPre()+ " e " + pasto.getPost());
        // System.out.println("oraInt < oraAttuale: " + oraInt + "<" + oraAttuale);
        if((pasto.getPre() == 0 || pasto.getPost() == 0) && (oraInt < oraAttuale)) {
            // System.out.println("controllo non passato per" + pasto.getPasto());
            return false;
        }else{
            // System.out.println("controllo passato per" + pasto.getPasto());
            return true ;
        }
    }

}



