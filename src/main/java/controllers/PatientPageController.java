package controllers;

import javafx.animation.Animation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.*;
import java.time.format.DateTimeFormatter;

import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Pasto;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import utility.UIUtils;
import view.LogInView;
import java.time.LocalDate;


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
    LocalDate oggi = LocalDate.now();
    private final ObservableList<Pasto> pastiData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //messageStart.setText("Qui puoi inserire le somministrazioni giornaliere pre e post pasto .");
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        LineChart<?, ?> chart = (LineChart<?, ?>) lineChart.getChildren().get(0);

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
        /*pastiData.addAll(
                new Pasto("Colazione", "08:00", "", ""),
                new Pasto("Pranzo", "13:00", "", ""),
                new Pasto("Cena", "19:30", "", "")
        );*/
        nuovaSomministrazioneButton.setOnAction(e -> nuovaSomministrazione());
        avviaPromemoria();
        caricaSomministrazioniOdierne();
        logOutButton.setOnAction(e -> LogOutButton());
        salvaSintomi.setOnAction(e -> salvaSintomi());
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

        /*int giorno = oggi.getDayOfMonth();
        int mese = oggi.getMonthValue(); // 1-12
        int anno = oggi.getYear();
        String dataFormattata = oggi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dataPerDatabase = oggi.toString();*/

        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, ID_terapia, rilevazione_pre_pasto, orario) VALUES ( ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            // salvo i dati nel db
            for (Pasto p : tableView.getItems()) {
                pstmt.setString(1,  oggi.toString() );
                pstmt.setFloat(2, Float.parseFloat(p.getPost()));
                pstmt.setString(3, "note...");
                pstmt.setInt(4, 2);
                pstmt.setFloat(5, Float.parseFloat(p.getPre()));
                pstmt.setString(6,p.getOrario());
                pstmt.executeUpdate();
            }

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

    private void caricaSomministrazioniOdierne() {
        String url = "jdbc:sqlite:miodatabase.db";
        LocalDate oggi = LocalDate.now();
        String dataOdierna = oggi.toString(); // es: 2025-07-03
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione == ?";

        pastiData.clear(); // Pulisce la tabella

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataOdierna);
            var rs = pstmt.executeQuery();
            boolean trovato = false;
            while (rs.next()) {
                System.out.println("ciao , sono nel while !");
                trovato = true;
                String orarioCompleto = rs.getString("data_rilevazione");
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

                pastiData.add(pasto);
            }

            // Se nessuna somministrazione trovata, carico la tabella vuota base
            if (!trovato) {
                pastiData.addAll(
                        new Pasto("Colazione", "08:00", "", ""),
                        new Pasto("Pranzo", "13:00", "", ""),
                        new Pasto("Cena", "19:30", "", "")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il caricamento delle somministrazioni.");
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

    private void salvaSintomi() {
        String testo = textArea.getText();
        System.out.println("Hai scritto: " + testo);
    }

}
