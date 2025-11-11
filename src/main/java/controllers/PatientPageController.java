package controllers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import DAO.PatientPaneDao;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.util.converter.FloatStringConverter;
import DAO.PatientPageDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import models.*;
import utility.SessionManager;
import utility.UIUtils;
import view.ChatPageView;
import view.DataUserView;
import view.PatientPageView;
import view.UserProfileView;

import java.time.LocalDate;
import java.util.*;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

/**
 * Controller della pagina del paziente.
 * @packege controllers
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/PatientPage.fxml">PatientPage.fxml</a>
 */
public class PatientPageController {
    @FXML private Label notificationBadge;
    @FXML private TableView<Pasto> tableView;
    @FXML private TableColumn<Pasto, String> pastoColumn;
    @FXML private TableColumn<Pasto, Float> preColumn;
    @FXML private TableColumn<Pasto, Float> postColumn;
    @FXML private TableColumn<Pasto, String> orarioColumn;
    @FXML private Label messageStart, infoPaziente;
    @FXML private Button logOutButton, nuovaSomministrazioneButton, salvaSintomi, provaAccount, chatB, myData;
    @FXML private TextArea textArea;
    /**
     * Lista con al suo interno i Pasti
     * @see models.Pasto
     */
    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();
    @FXML private PatientChartController chartIncludeController;
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientPageDao
     */
    private PatientPageDao dao;
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientPaneDao
     */
    private PatientPaneDao dao2;
    /**
     *
     */
    private boolean hasNotification = false;
    /**
     * Valore pre pasto minimo
     */
    private static final int PREPASTOMIN = 80;
    /**
     * Valore pre pasto massimo
     */
    private static final int PREPASTOMAX = 130;
    /**
     * Valore post pasto max
     */
    private static final int POSTPASTOMAX = 180;

    /**
     * Questo metodo ha lo scopo di inizializzare.
     * @see utility.UIUtils
     * @see DAO.PatientPaneDao
     * @see DAO.PatientPageDao
     */
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

        pastoColumn.setEditable(false);
        orarioColumn.setEditable(false);
        preColumn.setEditable(true);
        postColumn.setEditable(true);


        // Rende le celle editabili
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

        caricaSomministrazioniOdierne(SessionManager.getCurrentUser());
        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));
        salvaSintomi.setOnAction(e -> salvaSintomibox(textArea.getText()));
        caricaInfoPaziente(SessionManager.getCurrentUser());

        recuperoNotifiche();

        Platform.runLater(() -> checkSommOdierne());

        provaAccount.setOnAction(e -> {
            try {
                profilo((Stage) provaAccount.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        myData.setOnAction(e -> {
            try {
                myDataPage((Stage) provaAccount.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
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
    private void openChat() throws Exception {
        // Verifico che l'utente sia loggato
        if (SessionManager.getCurrentUser() == null || SessionManager.getCurrentRole() == null) {
            System.out.println("Errore: utente non loggato o ruolo non definito.");
            return;
        }

        // Verifico che il ruolo sia effettivamente "patient"
        if (!SessionManager.getCurrentRole().equals("paziente")) {
            System.out.println("Accesso negato: solo i pazienti possono aprire questa chat.");
            return;
        }

        // Determina il medico con cui il paziente deve chattare
        String assignedDoctor = UIUtils.getDoctor(SessionManager.getCurrentUser()); // TODO: prendo dinamicamente il medico assegnato

        toggleNotifiche();

        // Carica l'interfaccia della chat
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatPage.fxml"));
        Parent root = loader.load();

        // Ottieni il controller della chat e inizializza la conversazione
        ChatController chatController = loader.getController();
        chatController.initializeChat(SessionManager.getCurrentUser(), assignedDoctor);

        // Crea una nuova finestra per la chat
        Stage stage = new Stage();
        stage.setTitle("Chat con " + assignedDoctor);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icona_dottore.jpg")));
        new ChatPageView().start(stage);
        stage.setScene(new Scene(root, 500, 400));
        stage.show();

    }

    /**
     * Questo metodo ha lo scopo di aggiungere una notifica, nel caso ce ne fosse almeno una.
     */
    private void aggiungiNotifica() {
        notificationBadge.setText(String.valueOf(1));
        notificationBadge.setVisible(true);
    }

    /**
     * Questo metodo ha lo scopo di mostrare il numero di notifiche al paziente.
     * @see DAO.PatientPageDao
     */
    private void toggleNotifiche() {
        if (hasNotification) {
            // Se l'utente apre la chat, azzeriamo il contatore
            String paziente = SessionManager.getCurrentUser();
            dao.cambioVisualizzato(UIUtils.getDoctor(paziente), paziente);
            notificationBadge.setVisible(false);
            hasNotification = false;
        }
    }

    /**
     * Questo metodo ha lo scopo di recuperare le notifich.
     * @see DAO.PatientPageDao
     */
    private void recuperoNotifiche(){
        hasNotification = dao.recuperoNotifica(SessionManager.getCurrentUser());
        if(hasNotification) aggiungiNotifica();
    }

    /**
     * Questo metodo ha lo scopo di creare una nuova somministrazione.
     * Controlla per ogni pasto se esiste già un record con quella data e orario, se esiste: non lo inserisco.
     * Se non esiste e pre/post sono validi, lo inserisce.
     * Mostra un riepilogo solo dei pasti inseriti.
     * @see DAO.DoctorPageDao
     * @see utility.UIUtils
     */
    private void nuovaSomministrazione() {
        LocalDate oggi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder riepilogo = new StringBuilder("Riepilogo somministrazione:\n");
        Day rilevazioneGiorno = new Day();

        for (Pasto p : tableView.getItems()){
            String orario = p.getOrario();
            if (!dao.checkSomministarazione(orario, oggi, formatter, SessionManager.getCurrentUser()))
                continue; // Esiste già, quindi salto
            float pre = p.getPre();
            float post = p.getPost();

            if(pre < 0 && post > 250 ){
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :", "Sono stati inseriti dei valori anomali .");
                return ;
            }

            // controllo valori somministrazione
            if((pre < PREPASTOMIN || pre > PREPASTOMAX || post > POSTPASTOMAX ) && (pre != 0 && post != 0)) {
                UIUtils.showAlert(Alert.AlertType.WARNING, "Valori somministrazione", "I valori di questa somministrazione sono fuori dal range : ");
                // mess di def. somm
                String patient = SessionManager.getCurrentUser();
                dao.messageSomm(autoNotificationContent(pre, post , orario, p.getPasto(), rilevazioneGiorno.getDataString()), UIUtils.getDoctor(patient), patient);
            }
            rilevazioneGiorno.addPasto(new Pasto(null,orario,pre,post));

            riepilogo.append("🍽 ")
                    .append(p.getPasto())
                    .append(" (").append(orario).append("): ")
                    .append("Pre = ").append(pre).append(", ")
                    .append("Post = ").append(post).append("\n");
        }

        if (dao.addSomministrazione(rilevazioneGiorno, SessionManager.getCurrentUser())) {
            stampaTabella();
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Somministrazione salvata", riepilogo.toString());
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessun pasto inserito", "Tutti i pasti erano già presenti o non validi (pre/post nulli o 0).");
        }
    }

    /**
     * Questo metodo ha lo scopo di creare il messaggio da mandare al medico nel caso in cui l'utente inserisce dei valori fuori dal renge consentito.
     * @param pre    somministrazione pre pasto
     * @param post   somministrazione post pasto
     * @param orario orario somministrazione
     * @param pasto  pasto somministrazione
     * @param data   data somministrazione
     * @return String - messaggio da inviare al medico
     */
    private String autoNotificationContent(float pre , float post, String orario, String pasto, String data){
        return "Messaggio somministrazione : oggi " + data + " , " + pasto + " ore "+ orario +" pre : " +
                pre + " e post "+ post + " i valore/i sono fuori dal range consentito.";
    }

    /**
     * Questo metodo ha lo scopo di caricare le somministrazioni odierne.
     * @param username nome utente
     * @see DAO.PatientPageDao
     */
    private void caricaSomministrazioniOdierne(String username) {
        pastiData.clear(); // Pulisce la tabella
        Map<String, Pasto> sommRilevati = new HashMap<>(dao.somministrazioneTabella(username));

        // Definisci gli orari attesi
        Map<String, String> orariPrevisti = new LinkedHashMap<>();  // new LinkedHashMap<>(): simile as HashMap ma mantiene l'ordine di inserimento degli elementi
        orariPrevisti.put("08:00", "Colazione");
        orariPrevisti.put("13:00", "Pranzo");
        orariPrevisti.put("19:30", "Cena");

        // Aggiunge i pasti ordinati (colazione, pranzo, cena)
        for (Map.Entry<String, String> entry : orariPrevisti.entrySet()) {
            String orario = entry.getKey();
            String nome = entry.getValue();

            if (sommRilevati.containsKey(orario)) {
                pastiData.add(sommRilevati.get(orario));
            } else {
                pastiData.add(new Pasto(nome, orario, 0, 0));
            }
        }
    }

    /**
     * Questo metodo ha lo scopo di salvare i sintomi inseriti dall'utente:
     * salvo la nota nell'ultima somministrazione inserita nel giorno, nel caso in cui non si ha nessuna somministrazione oggi
     * controllo se l'ultima somministrazione del giorno precedente ha una nota != "note..." , se true la sovrascrivo,
     * se false mando un alert dicendo di contattare il medico o di aspettare di inserire una somministrazione.
     * @param nuovaNota  sintomi inseriti dal paziente
     * @see DAO.PatientPageDao
     */
    private void salvaSintomibox(String nuovaNota){
        // salvo nel database (in "note_rivelazione") ciò che l'utente scrive nel box sintomi che poi verrà mostrato al medico
        LocalDate oggi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(dao.cercoSintomi(nuovaNota, SessionManager.getCurrentUser(), oggi, formatter) ){
            System.out.println("operazione riuscita");
        }
        else{
            System.out.println("operazione non riuscita");
        }

        /*if(!dao.cercoSintomiOggi(oggi, formatter, nuovaNota, SessionManager.currentUser)){// 1. Controllo se ci sono somministrazioni oggi
            dao.cercoSintomiGiorniPrecedenti(nuovaNota, SessionManager.currentUser);  // 2. Nessuna somministrazione oggi → cerco l’ultima disponibile
        }*/
    }

    /**
     * Questo metodo ha lo scopo di mostrare a video il contenuto della tabella inserito dall'utente.
     */
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

    /**
     * Questo metodo ha lo scopo di caricare le informazioni del paziente
     * @param username
     * @see DAO.PatientPaneDao
     */
    private void caricaInfoPaziente(String username){
        infoPaziente.setText(dao2.getInfoUtente(username));
    }

    /**
     * Questo metodo ha lo scopo di controllare se l'utente non ha inserito una o più somministrazioni oggi.
     * Nel caso in cui l'utente non inserisce delle somministrazioni da almeno 3 giorni mando un messaggio al dottore.
     * Viene lanciato un ALert nel caso mancassero o si è in ritardo nell'inserimento delle somministrazioni delle ore precedenti.
     * @see DAO.PatientPageDao
     * @see utility.UIUtils
     * @see models.ChartFilter
     */
    private void checkSommOdierne() {
        LocalTime ora = LocalTime.now();
        int oraAttuale = ora.getHour(); // restituisce solo l'ora (0-23)

        final String[] s = {""};
        tableView.getItems().forEach((Pasto p) -> {
            if (controllo(p , oraAttuale)){
                s[0] +="\n  - " + p.getPasto();
            }
        });
        if(! s[0].isEmpty()) {
            // warning all'utente
            UIUtils.showAlert(Alert.AlertType.WARNING, "Attenzione", "Mancano le rilevazioni delle ore precedenti \n"+ s[0]);
            /*
            // messaggio al medico
            // data2 è la data di oggi e data1 è la data di 3 giorni prima di data2, controllo se data1 è nel db
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
            String patient = SessionManager.getCurrentUser();
            if(dao.messageSommDim( filter, patient)) {
                // content mess al dottore
                String content = "Mancano delle somministrazioni da parte di " + patient + " da almeno 3 giorni . Oggi è il : " + data2;
                // invio mess al dottore

                if(! dao.messDuplicato(content, UIUtils.getDoctor(patient), patient)) {
                    dao.messageSomm(content, UIUtils.getDoctor(patient), patient);
                }
            }*/
        }
    }

    /**
     * Questo metodo ha lo scopo di controllare se l'utente è in ritardo con la rilevazione.
     * @param pasto pasto somministrazione
     * @param oraAttuale ora attuale
     * @return valore booleano che indica l'esito della valutazione
     */
    private boolean controllo(Pasto pasto, int oraAttuale) {
        LocalTime time = LocalTime.parse(pasto.getOrario(),DateTimeFormatter.ofPattern("HH:mm") );
        int oraInt = time.getHour();
        return (pasto.getPre() == 0 || pasto.getPost() == 0) && (oraInt < oraAttuale);
    }

    /**
     * Questo metodo ha lo scopo di creare la nuova finestra 'profilo utente'
     * @param stage
     * @throws Exception
     * @see view.UserProfileView
     */
    private void profilo(Stage stage) throws Exception {
        String username = SessionManager.getCurrentUser();
        new UserProfileView(username).start(stage);
    }

    /**
     * Questo metodo ha lo scopo di creare la nuova finestra 'dati utente'
     * @param stage
     * @throws Exception
     * @see view.DataUserView
     */
    private void myDataPage(Stage stage) throws Exception {
        //String username = SessionManager.getCurrentUser();
        new DataUserView().start(stage);
    }


}



