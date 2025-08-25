package controllers;

import DAO.PatientChartDao;
import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.ChartDataInstance;
import models.FilterDataSetter;
import utility.UIUtils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller della finestra che permette la scelta dei filtri per la visualizzazione del grafico del paziente.
 * Questa classe permette di impostare filtri come lo stato della terapia, il tipo di serie, il farmaco utilizzato
 * e l'intervallo di date desiderato.
 *
 * @package controllers
 * @see DAO.PatientChartDao
 * @see enums.StatoTerapia
 * @see utility.UIUtils
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/SceltaOpzione.fxml">SceltaOpzione.fxml</a>
 */
public class SceltaOpzioneController {

    // Attributi della classe
    @FXML private ComboBox<String> SerieComboBox;
    @FXML private ComboBox<String> FarmacoComboBox;
    @FXML private ComboBox<String> DataInizioComboBox;
    @FXML private ComboBox<String> DataFineComboBox;
    @FXML private CheckBox check1;
    @FXML private CheckBox check2;
    @FXML private CheckBox check3;

    private static final PatientChartDao dao = new PatientChartDao(); // DAO per recuperare dati relativi alle rilevazioni del paziente
    /**
     * Valore numerico per rappresentare lo stato della terapia selezionato
     */
    private int valoreStatoSelezionato = 0;
    /**
     * Serie selezionata dall’utente
     */
    private String valoreSerieSelezionato = ChartDataInstance.ALL_TEXT;
    /**
     * Farmaco selezionato
     */
    private String valoreFarmacoSelezionato = null;
    /**
     * Username del paziente di riferimento
     */
    private String username = null;
    /**
     * Data di inizio selezionata
     */
    private String dataInizio = null;
    /**
     * Data di fine selezionata
     */
    private String dataFine = null; //
    /**
     * Mappa che associa nome serie al rispettivo ID
     */
    private final HashMap<String,Integer> serieMap = new HashMap<>();

    /**
     * Metodo di inizializzazione automatico eseguito al caricamento del controller.
     * Imposta i testi dei checkbox relativi agli stati della terapia e popola la comboBox delle serie.
     * @see enums.StatoTerapia
     */
    @FXML
    public void initialize() {
        check1.setText(StatoTerapia.ATTIVA.getStato());
        check2.setText(StatoTerapia.SOSPESA.getStato());
        check3.setText(StatoTerapia.TERMINATA.getStato());
        check1.setUserData(FilterDataSetter.ON_GOING_STATUS);
        check2.setUserData(FilterDataSetter.ON_PAUSE_STATUS);
        check3.setUserData(FilterDataSetter.TERMINATED_STATUS);
        int i = 0;
        for (String s : ChartDataInstance.TEXT_LIST) {
            SerieComboBox.getItems().add(s);
            serieMap.put(s,i);
            i++;
        }
    }

    /**
     * Restituisce il valore numerico combinato degli stati di terapia selezionati.
     * @return int - valore stato terapia
     */
    public int getValoreStatoSelezionato() {
        return valoreStatoSelezionato;
    }

    /**
     * Restituisce il farmaco selezionato.
     * @return String - nome del farmaco
     */
    public String getValoreFarmacoSelezionato() {
        return valoreFarmacoSelezionato;
    }

    /**
     * Restituisce la data di inizio selezionata.
     * @return String - data inizio
     */
    public String getDataInizio() {
        return dataInizio;
    }

    /**
     * Restituisce la data di fine selezionata.
     * @return String - data fine
     */
    public String getDataFine() {
        return dataFine;
    }

    /**
     * Restituisce l'ID della serie selezionata sulla base della mappa serieMap.
     * Se la serie non è presente, restituisce MAX_ID (valore massimo definito).
     * @return int - id serie
     * @see models.ChartDataInstance
     */
    public int getValoreSerieSelezionato() {
        return serieMap.getOrDefault(valoreSerieSelezionato, ChartDataInstance.MAX_ID);
    }

    /**
     * Imposta lo username del paziente di cui si vogliono visualizzare i dati.
     * Questo valore è necessario per caricare i farmaci e le date delle rilevazioni.
     * @param username - username del paziente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Metodo che carica le comboBox dei farmaci e delle date in base allo username impostato.
     * @see utility.UIUtils
     * @see DAO.PatientChartDao
     */
    public void setWindowsData() {
        ArrayList<String> farmaci = UIUtils.getFarmaciPaziente(username);
        FarmacoComboBox.getItems().addAll(farmaci);
        ArrayList<String> date =  dao.getDateRilevazioni(username);
        DataInizioComboBox.getItems().addAll(date);
        DataFineComboBox.getItems().addAll(date);
    }

    /**
     * Metodo invocato al momento della conferma della scelta da parte dell’utente.
     * Raccoglie tutti i filtri selezionati e li salva in variabili di istanza.
     * Infine chiude la finestra attiva.
     * @param event - evento di azione generato dal bottone
     */
    @FXML
    private void confermaScelta(javafx.event.ActionEvent event) {
        valoreStatoSelezionato = 0;
        if (check1.isSelected()) valoreStatoSelezionato += FilterDataSetter.ON_GOING_STATUS;
        if (check2.isSelected()) valoreStatoSelezionato += FilterDataSetter.ON_PAUSE_STATUS;
        if (check3.isSelected()) valoreStatoSelezionato += FilterDataSetter.TERMINATED_STATUS;
        valoreFarmacoSelezionato = FarmacoComboBox.getValue();
        dataInizio = DataInizioComboBox.getValue();
        dataFine = DataFineComboBox.getValue();
        valoreSerieSelezionato = SerieComboBox.getValue();

        // Chiude la finestra
        Stage stage = (Stage) FarmacoComboBox.getScene().getWindow();
        stage.close();
    }

}
