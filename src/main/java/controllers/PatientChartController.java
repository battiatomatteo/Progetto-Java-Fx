package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import models.ChartDataInstance;
import models.ChartDataSetter;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void setData(ChartDataSetter setter){
        PatientChart.getData().clear();
        setChartData(setter);
    }

    private void setChartData(ChartDataSetter setter) {
        HashMap<Integer,String> chartData;
        String username = setter.getPatientUserName();
        chartData = dao.getTerapiePaziente(setter);

       chartData.forEach((idTerapia,farmaco)->{
            ChartDataInstance data = new ChartDataInstance(idTerapia,farmaco,ChartDataInstance.PRE, ChartDataInstance.POST);
            setChartSeries(data, username, idTerapia);
            PatientChart.getData().addAll(data.getSeriesDataList());
       });
    }
    private void setChartSeries(ChartDataInstance series, String username, int IdTerapia) {
        ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username,IdTerapia);
        series.addSeriesData(rilevazioni);
    }
}