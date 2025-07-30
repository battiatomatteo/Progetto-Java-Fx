package models;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class ChartDataInstance {
    private  XYChart.Series<String, Number> seriesData;
    private int idTerapia;
    private String farmaco;
    private int id;


    public static final int PRE = 1;
    public static final int POST = 2;
    private static final String PRE_TEXT = "(prima dei pasti)";
    private static final String POST_TEXT = "(dopo dei pasti)";
/*
    public ChartDataInstance(int idTerapia,String farmaco, String seriesName) {
        ChartDataInstance(idTerapia,farmaco);
        seriesData.setName(seriesName);
    }*/

    public ChartDataInstance(int idTerapia,String farmaco,int textId) {
        ChartDataInstance(idTerapia,farmaco);
        String name = switch (textId) {
            case PRE -> farmaco + PRE_TEXT;
            case POST -> farmaco + POST_TEXT;
            default -> throw new RuntimeException("nome del grafico non valido");
        };
        seriesData.setName(name);


        id = textId;
    }
    private void ChartDataInstance(int idTerapia,String farmaco){
        seriesData = new XYChart.Series<String, Number>();
        this.idTerapia = idTerapia;
        this.farmaco = farmaco;
    }

    public XYChart.Series<String, Number> getSeriesData() {
        return seriesData;
    }

    public int getIdTerapia() {
        return idTerapia;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public void addSeriesData(ArrayList<Rilevazioni> rilevazioni,int idTerapia) {
       for (Rilevazioni r : rilevazioni) {
           addDataEntry(r, idTerapia);
       }
    }

    private void addDataEntry(Rilevazioni r,int idp) {
        if(idp == 1){
            newChartItem(seriesData,r.getDate(),r.getRilevazionePrePasto());
        }
        if(idp == 2){
            newChartItem(seriesData,r.getDate(),r.getRilevazionePostPasto());
        }
    }

    private void newChartItem(XYChart.Series<String, Number> series, String param1, float param2 ) {
        series.getData().add(new XYChart.Data<>(param1, param2));
    }
}
