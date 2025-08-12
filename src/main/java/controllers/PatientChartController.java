package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import models.ChartDataInstance;
import java.util.ArrayList;
import DAO.PatientChartDao;
import models.ChartFilter;
import models.Rilevazioni;

public class PatientChartController {
    @FXML private LineChart<String, Number> PatientChart;  // Il grafico a linee
    private PatientChartDao dao;

    @FXML
    public void initialize() {
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");   // Imposta il titolo del grafico
        dao = new PatientChartDao();
    }

    public void setData(String username , ChartFilter filter){
        PatientChart.getData().clear();
        setChartData(username, filter);
    }

    private void setChartData(String username, ChartFilter filter) {
        ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username, filter);
        if (rilevazioni != null && !rilevazioni.isEmpty()) {
            ChartDataInstance data = new ChartDataInstance();
            data.addSeriesData(rilevazioni);  // popola i dati
            switch (filter.getSeriesID()){
                case ChartDataInstance.PRE:{
                    PatientChart.getData().add(data.getSeriesById(ChartDataInstance.PRE));
                    return;
                }
                case ChartDataInstance.POST:{
                    PatientChart.getData().add(data.getSeriesById(ChartDataInstance.POST));
                    return;
                }
                case ChartDataInstance.MAX_ID:{
                    PatientChart.getData().addAll(data.getSeriesDataList());
                }
            }

        }
    }
}