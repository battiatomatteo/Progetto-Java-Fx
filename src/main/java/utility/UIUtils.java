package utility;

import DAO.UIUtilsDao;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import enums.GiorniSettimana;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Terapia;
import view.LogInView;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class UIUtils {
    private static final UIUtilsDao dao = new UIUtilsDao();

    //  mostra l'Alert
    public static void showAlert(Alert.AlertType t, String title, String txt) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }

    // metodo authenticate, in base al tipo 'flag' = 0 o 'flag' = 1 esegue un tipo di autenticazione
    public static boolean authenticate(String username, String password, int flag) {
        if(flag == 0 ){
             return dao.authenticateLogin(username, password);
        } else {
            return dao.authenticatePatient(username);
        }
    }

    public static void LogOutButton(Stage stage){
        try {
            //Stage stage = (Stage) logOutButton.getScene().getWindow();
            // JFXPanel logOutButton = new JFXPanel();
            stage.close();
            new LogInView().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String dataOggi(){
        LocalDate oggi = LocalDate.now();
        return oggi.toString();
    }

    public static String dataConGiorno(){
        LocalDate oggi = LocalDate.now();
        DayOfWeek giorno = oggi.getDayOfWeek(); // esempio MONDAY: mi restituisce il giorno in inglese 
        
        // calcolo giorno in italiano
        // Ottieni l'indice (1-7), poi usa -1 perché gli array partono da 0
        int ind = giorno.getValue() - 1;
        GiorniSettimana giornoSettimana =  GiorniSettimana.values()[ind];

        return giornoSettimana + " " + oggi.toString();
    }

    public static void generaPDFReport(Stage stage, TextField usernameInput, VBox chartInclude, TableView<Terapia> table) { // passa lo Stage principale qui
        String nomePaziente = usernameInput.getText();
        try {
            // FileChooser per salvare file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            fileChooser.setInitialFileName(nomePaziente + ".pdf");

            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile == null) {
                return; // utente ha annullato
            }

            // 1. Crea documento
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
            document.open();

            // 2. Titolo e nome paziente
            Font titoloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph titolo = new Paragraph("Report Paziente", titoloFont);
            titolo.setAlignment(Element.ALIGN_CENTER);
            document.add(titolo);
            document.add(new Paragraph("Nome: " + nomePaziente));
            document.add(Chunk.NEWLINE);

            // 3. Tabella PDF
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            pdfTable.addCell("Terapia");
            pdfTable.addCell("stato");
            pdfTable.addCell("Farmaco");
            pdfTable.addCell("Assunzioni");
            pdfTable.addCell("Quantità");
            pdfTable.addCell("Note");

            for (Terapia terapia : table.getItems()) {
                pdfTable.addCell(String.valueOf(terapia.getIdTerapia()));
                pdfTable.addCell(terapia.getStatoEnum().toString());
                pdfTable.addCell(terapia.getFarmaco());
                pdfTable.addCell(terapia.getAssunzioni());
                pdfTable.addCell(terapia.getQuantita());
                pdfTable.addCell(terapia.getNote());
            }

            document.add(pdfTable);
            document.add(Chunk.NEWLINE);

            // 4. Grafico come immagine
            WritableImage snapshot = chartInclude.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            File chartFile = new File("grafico.png");
            ImageIO.write(bufferedImage, "png", chartFile);

            Image chartImage = Image.getInstance("grafico.png");
            chartImage.scaleToFit(500, 300);
            chartImage.setAlignment(Image.MIDDLE);
            document.add(chartImage);

            // 5. Chiudi
            document.close();
            chartFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile generare il PDF.");
        }
    }

    public static void filtraTerapia(String username){

        // Nuovo Stage (finestra)
        Stage finestraFiltro = new Stage();
        finestraFiltro.setTitle("Filtri Ricerca");

        // ComboBox per Stato terapia
        ComboBox<String> statoCombo = new ComboBox<>();
        statoCombo.getItems().addAll("Tutti", "Attiva", "Sospesa", "Terminata");
        statoCombo.setValue("Tutti");

        // ComboBox per Tipo farmaco
        ComboBox<String> tipoFarmacoCombo = new ComboBox<>();
        tipoFarmacoCombo.getItems().addAll(dao.getFarmaciPaziente(username));

        //tipoFarmacoCombo.getItems().addAll("Tutti", "Antibiotico", "Antidolorifico", "Altro");

        tipoFarmacoCombo.setValue("Tutti");

        // Pulsante per filtrare
        Button filtraBtn = new Button("Applica Filtri");
        filtraBtn.setOnAction(e -> {
            String stato = statoCombo.getValue();
            String tipoFarmaco = tipoFarmacoCombo.getValue();

            // TODO: Chiama qui il tuo metodo di filtro reale
            System.out.println("Filtro applicato:");
            System.out.println(" - Stato: " + stato);
            System.out.println(" - Tipo farmaco: " + tipoFarmaco);

            finestraFiltro.close(); // chiudi la finestra dopo il filtro (opzionale)
        });

        // Layout finestra
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Stato Terapia:"), statoCombo,
                new Label("Tipo Farmaco:"), tipoFarmacoCombo,
                filtraBtn
        );

        Scene scena = new Scene(layout, 300, 200);
        finestraFiltro.setScene(scena);
        finestraFiltro.show();

    }

    public static String getDoctor(String username){
        return dao.getDoctorUser(username);

    }
    public static void printMessage(String message){
        System.out.println(message);
    }
}
