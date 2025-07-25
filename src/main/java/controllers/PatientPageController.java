package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.ChartDataSetter;
import models.Pasto;
import utility.UIUtils;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class PatientPageController {
    @FXML private TableView<Pasto> tableView;
    @FXML private TableColumn<Pasto, String> pastoColumn;
    @FXML private TableColumn<Pasto, String> preColumn;
    @FXML private TableColumn<Pasto, String> postColumn;
    @FXML private TableColumn<Pasto, String> orarioColumn;
    @FXML private Label messageStart;
    @FXML private Button logOutButton, nuovaSomministrazioneButton, salvaSintomi;
    @FXML private TextArea textArea;
    @FXML private VBox lineChart;
    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();
    @FXML private PatientChartController chartIncludeController;

    @FXML
    private void initialize() {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        messageStart.setText("Qui puoi inserire le somministrazioni giornaliere pre e post pasto di  " + UIUtils.dataConGiorno()  );
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

        nuovaSomministrazioneButton.setOnAction(e -> nuovaSomministrazione());
        caricaSomministrazioniOdierne();
        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));
        salvaSintomi.setOnAction(e -> salvaSintomibox(textArea.getText()));

        chartIncludeController.setData(new ChartDataSetter(LogInController.getUsername(), ChartDataSetter.ALL)); // passo il nome del paziente
    }

    /*
     * Cosa fa nuovaSomministrazione() :
     * Controlla per ogni pasto se esiste già un record con quella data e orario, Se esiste: non lo reinserisce.
     * Se non esiste e pre/post sono validi, lo inserisce.
     * Mostra un riepilogo solo dei pasti inseriti.
     * */
    private void nuovaSomministrazione() {
        String url = "jdbc:sqlite:miodatabase.db";
        String sqlInsert = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, ID_terapia, rilevazione_pre_pasto, orario) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlCheck = "SELECT COUNT(*) FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND orario = ?";
        LocalDate oggi = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Connection conn = DriverManager.getConnection(url)) {
            try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert);
                 PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {

                StringBuilder riepilogo = new StringBuilder("Riepilogo somministrazione:\n");
                boolean almenoUnoInserito = false;

                for (Pasto p : tableView.getItems()) {
                    String preStr = p.getPre();
                    String postStr = p.getPost();
                    String orario = p.getOrario();

                    if (preStr == null || preStr.isEmpty() || postStr == null || postStr.isEmpty() || orario == null || orario.isEmpty())
                        continue;

                    float pre = Float.parseFloat(preStr);
                    float post = Float.parseFloat(postStr);

                    if (pre == 0 || post == 0)
                        continue;

                    // Verifica se esiste già una rilevazione per oggi con questo orario
                    checkStmt.setString(1, oggi.format(formatter));
                    checkStmt.setString(2, orario);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        continue; // Esiste già, quindi salto
                    }

                    // Inserisci solo se non esiste già
                    insertStmt.setString(1, oggi.format(formatter));
                    insertStmt.setFloat(2, post);
                    insertStmt.setString(3, "note...");
                    insertStmt.setInt(4, 2);
                    insertStmt.setFloat(5, pre);
                    insertStmt.setString(6, orario);
                    insertStmt.executeUpdate();

                    almenoUnoInserito = true;

                    riepilogo.append("🍽 ")
                            .append(p.getPasto())
                            .append(" (").append(orario).append("): ")
                            .append("Pre = ").append(pre).append(", ")
                            .append("Post = ").append(post).append("\n");
                }

                if (almenoUnoInserito) {
                    stampaTabella();
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Somministrazione salvata", riepilogo.toString());
                } else {
                    UIUtils.showAlert(Alert.AlertType.WARNING, "Nessun pasto inserito", "Tutti i pasti erano già presenti o non validi (pre/post nulli o 0).");
                }

            }
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private void caricaSomministrazioniOdierne() {
        String url = "jdbc:sqlite:miodatabase.db";
        //LocalDate oggi = LocalDate.now();
        //String dataOdierna = oggi.toString();
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione == ?";

        pastiData.clear(); // Pulisce la tabella

        Map<String, Pasto> rilevati = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, UIUtils.dataOggi());
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                String orario = rs.getString("orario");
                String pre = Float.toString(rs.getFloat("rilevazione_pre_pasto"));
                String post = Float.toString(rs.getFloat("rilevazione_post_pasto"));

                String nomePasto = switch (orario) {
                    case "08:00" -> "Colazione";
                    case "13:00" -> "Pranzo";
                    case "19:30" -> "Cena";
                    default -> "Pasto";
                };

                Pasto pasto = new Pasto(nomePasto, orario, pre, post);
                rilevati.put(orario, pasto);
            }

            // Definisci gli orari attesi
            Map<String, String> orariPrevisti = new LinkedHashMap<>();  // new LinkedHashMap<>(): simile as HashMap ma mantiene l'ordine di inserimento degli elementi
            orariPrevisti.put("08:00", "Colazione");
            orariPrevisti.put("13:00", "Pranzo");
            orariPrevisti.put("19:30", "Cena");

            // Aggiunge i pasti ordinati (colazione, pranzo, cena)
            for (Map.Entry<String, String> entry : orariPrevisti.entrySet()) {
                String orario = entry.getKey();
                String nome = entry.getValue();

                if (rilevati.containsKey(orario)) {
                    pastiData.add(rilevati.get(orario));
                } else {
                    pastiData.add(new Pasto(nome, orario, "", ""));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il caricamento delle somministrazioni.");
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

}