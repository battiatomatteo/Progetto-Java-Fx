package controllers;

import DAO.PatientChartDao;
import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.ChartDataInstance;
import models.ChartFilter;
import models.FilterDataSetter;
import models.Rilevazioni;
import utility.UIUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SceltaOpzioneController {
    @FXML private ComboBox<String> SerieComboBox;
    @FXML private ComboBox<String> FarmacoComboBox;
    @FXML private ComboBox<String> DataInizioComboBox;
    @FXML private ComboBox<String> DataFineComboBox;
    @FXML private CheckBox check1;
    @FXML private CheckBox check2;
    @FXML private CheckBox check3;

    private static final PatientChartDao dao = new PatientChartDao();

    private int valoreStatoSelezionato = 0;
    private String valoreSerieSelezionato = ChartDataInstance.ALL_TEXT;
    private String valoreFarmacoSelezionato = null;
    private String username = null;
    private String dataInizio = null;
    private String dataFine = null;
    private HashMap<String,Integer> serieMap = new HashMap<>();

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
        UIUtils.printMessage(serieMap.toString());
    }

    public int getValoreStatoSelezionato() {
        return valoreStatoSelezionato;
    }

    public String getValoreFarmacoSelezionato() {
        return valoreFarmacoSelezionato;
    }

    public String getDataInizio() {
        return dataInizio;
    }
    public String getDataFine() {
        return dataFine;
    }
    public int getValoreSerieSelezionato() {
        return serieMap.getOrDefault(valoreSerieSelezionato, ChartDataInstance.MAX_ID);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWindowsData() {
        ArrayList<String> farmaci = UIUtils.getFarmaciPaziente(username);
        FarmacoComboBox.getItems().addAll(farmaci);
        ArrayList<String> date =  dao.getDateRilevazioni(username);
        DataInizioComboBox.getItems().addAll(date);
        DataFineComboBox.getItems().addAll(date);

    }

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
