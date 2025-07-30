package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import models.ChartDataInstance;
import models.ChartDataSetter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import DAO.PatientChartDao;
import models.Rilevazioni;

public class PatientChartController {

    @FXML
    private LineChart<String, Number> PatientChart;  // Il grafico a linee
    @FXML private CategoryAxis xAxis;                      // Asse X (Categoria)
    @FXML private NumberAxis yAxis;                        // Asse Y (Numerico)
    private PatientChartDao dao;

    @FXML
    public void initialize() {

        // Imposta il titolo del grafico
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");
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
            ChartDataInstance pre = new ChartDataInstance(idTerapia,farmaco,ChartDataInstance.PRE);
            ChartDataInstance post = new ChartDataInstance(idTerapia,farmaco,ChartDataInstance.POST);
            setChartSeries(pre,post, username, idTerapia);
            PatientChart.getData().add(pre.getSeriesData());
            PatientChart.getData().add(post.getSeriesData());
       }
       );

    }



    private void setChartSeries(ChartDataInstance series_pre ,ChartDataInstance series_post , String username, int IdTerapia) {
        ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username,IdTerapia);
        series_pre.addSeriesData(rilevazioni,1);
        series_post.addSeriesData(rilevazioni,2);
    }

    private void newChartItem(XYChart.Series<String, Number> series, String param1, int param2 ) {
        series.getData().add(new XYChart.Data<>(param1, param2));
    }

}