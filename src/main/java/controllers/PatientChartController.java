package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import models.ChartDataInstance;
import models.ChartDataSetter;
import java.util.ArrayList;
import java.util.HashMap;
import DAO.PatientChartDao;
import models.FilterDataSetter;
import models.Rilevazioni;

public class PatientChartController {
    @FXML private LineChart<String, Number> PatientChart;  // Il grafico a linee
    private PatientChartDao dao;

    @FXML
    public void initialize() {
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");   // Imposta il titolo del grafico
        dao = new PatientChartDao();
    }

    public void setData(FilterDataSetter setter){
        PatientChart.getData().clear();
        setChartData(setter);
    }

    private void setChartData(FilterDataSetter setter) {
        HashMap<Integer,String> chartData;
        String username = setter.getPatientUserName();
        chartData = dao.getTerapiePaziente(setter);

        chartData.forEach((idTerapia, farmaco) -> {
            // Prendi tutte le rilevazioni per questa terapia
            ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username, idTerapia);

            // Se ci sono rilevazioni (non null e non vuoto) allora aggiungi la serie al grafico
            if (rilevazioni != null && !rilevazioni.isEmpty()) {
                ChartDataInstance data = new ChartDataInstance(idTerapia, farmaco, ChartDataInstance.PRE, ChartDataInstance.POST);
                data.addSeriesData(rilevazioni);  // popola i dati
                PatientChart.getData().addAll(data.getSeriesDataList());
            }
            // altrimenti salta questa terapia e non la aggiunge al grafico
        });
    }
}