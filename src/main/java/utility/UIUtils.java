package utility;

import DAO.PatientPaneDao;
import DAO.UIUtilsDao;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import enums.GiorniSettimana;
import javafx.embed.swing.SwingFXUtils;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Classe UIUtils
 * @packege utility
 * @see DAO.UIUtilsDao
 * @see DAO.PatientPaneDao
 */
public class UIUtils {
    private static final UIUtilsDao dao = new UIUtilsDao();
    private static final PatientPaneDao daoP = new PatientPaneDao();

    /**
     * Metodo con lo scopo di creare l'Alert da lanciare
     * @param t tipo di Alert
     * @param title titolo Alert
     * @param txt testo Alert
     */
    public static void showAlert(Alert.AlertType t, String title, String txt) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }

    // metodo authenticate,

    /**
     * Metodo con lo scopo di controllare il tipo di autenticazione da svolgere, in base al tipo 'flag' = 0 o 'flag' = 1 esegue un tipo di autenticazione
     * @param username
     * @param password
     * @param flag
     * @return valore booleano
     * @see DAO.UIUtilsDao
     */
    public static boolean authenticate(String username, String password, int flag) {
        if(flag == 0 ){
             return dao.authenticateLogin(username, password);
        } else {
            return dao.authenticatePatient(username);
        }
    }

    /**
     * Metodo con lo scopo di far tornare alla pagina di log in
     * @param stage
     */
    public static void LogOutButton(Stage stage){
        try {
            stage.close();
            new LogInView().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo con lo scopo di restituire la data di oggi
     * @return stringa - data di oggi
     */
    public static String dataOggi(){
        LocalDate oggi = LocalDate.now();
        return oggi.toString();
    }

    /**
     * Metodo con lo scopo di restituire la data col giorno esatto, lo calcolo poi in italiano
     * @return stringa - giorno della settimana + data
     */
    public static String dataConGiorno(){
        LocalDate oggi = LocalDate.now();
        DayOfWeek giorno = oggi.getDayOfWeek(); // esempio MONDAY: mi restituisce il giorno in inglese 
        
        // calcolo giorno in italiano
        // Ottieni l'indice (1-7), poi usa -1 perché gli array partono da 0
        int ind = giorno.getValue() - 1;
        GiorniSettimana giornoSettimana =  GiorniSettimana.values()[ind];

        return giornoSettimana + " " + oggi;
    }

    /**
     * Metodo con lo scopo di creare un PDF con le informazioni del paziente cercato dal medico
     * @param stage  PatientPane, grafico, tabella e informazioni paziente
     * @param usernameInput  paziente cercato dal medico
     * @param chartInclude
     * @param table  tabella con tutti i valori delle terapie assegnate al paziente
     * @see models.Terapia
     */
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
                return; // l'utente ha annullato
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
            document.add(new Paragraph("Informazioni del paziente :\n" + daoP.getInfoUtente(nomePaziente)));
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

    /**
     * Metodo con lo scopo di recuperare il nome del dottore del paziente passato
     * @param username nome paziente
     * @return stringa - nome del dottore
     * @see DAO.UIUtilsDao
     */
    public static String getDoctor(String username){
        return dao.getDoctorUser(username);
    }

    /**
     * Metodo con lo scopo di recuperare i farmaci del paziente
     * @param username nome del paziente
     * @return ArrayList<String> coi nomi dei farmaci
     * @see DAO.UIUtilsDao
     */
    public  static ArrayList<String> getFarmaciPaziente(String username){
        return dao.getFarmaciPaziente(username);
    }

    /**
     * Metodo con lo scopo di stampare a video una stringa passata
     * @param message content da stampare
     */
    public static void printMessage(String message){
        System.out.println(message);
    }

    /**
     * Metodo con lo scopo di controllare la parola passata, deve contenere solo lettere altrimenti ritorna falso.
     * @param input parola da controllare
     * @return valore booleano, vero se la risposta è corretta, falso altrimenti.
     */
    public static boolean controlloParolaStringa(String input) {
        return input != null && input.matches("^[A-Za-z]+$");
    }

    /**
     * Metodo con lo scopo di controllare la password passata, deve contenere almeno 8 caratteri, una maiuscola e un numero.
     * @param password password da controllare
     * @return valore booleano, vero se la risposta è corretta, falso altrimenti.
     */
    public static boolean controlloPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    }

    /**
     * Metodo con lo scopo di controllare il numero passato
     * @param number numero da controllare
     * @return valore booleano, vero se la risposta è corretta, falso altrimenti.
     */
    public static boolean controlloFloat(String number){
        try {
            int n = Integer.parseInt(number);
            return true;
        }
        catch ( NumberFormatException e) {
            return false;
        }
    }

}
