package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import models.ChartDataInstance;
import java.util.ArrayList;
import DAO.PatientChartDao;
import models.Rilevazioni;

public class PatientChartController {
    @FXML private LineChart<String, Number> PatientChart;  // Il grafico a linee
    private PatientChartDao dao;

    @FXML
    public void initialize() {
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");   // Imposta il titolo del grafico
        dao = new PatientChartDao();
    }

    public void setData(String username){
        PatientChart.getData().clear();
        setChartData(username);
    }

    private void setChartData(String username) {
        ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username);
        if (rilevazioni != null && !rilevazioni.isEmpty()) {
            ChartDataInstance data = new ChartDataInstance(ChartDataInstance.PRE, ChartDataInstance.POST);
            data.addSeriesData(rilevazioni);  // popola i dati
            PatientChart.getData().addAll(data.getSeriesDataList());
        }
    }
}