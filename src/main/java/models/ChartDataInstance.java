package models;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class ChartDataInstance {
    //private  XYChart.Series<String, Number> seriesDataPre;
    //private  XYChart.Series<String, Number> seriesDataPost;
    private XYChart.Series<String, Number>[] seriesData = new XYChart.Series[2];
    private int idTerapia;
    private String farmaco;
    public static final int PRE = 1;
    public static final int POST = 2;
    private static final String PRE_TEXT = "(prima dei pasti)";
    private static final String POST_TEXT = "(dopo dei pasti)";

    public ChartDataInstance(int idTerapia,String farmaco, String seriesName1 , String seriesName2) {
        ChartDataInstance(idTerapia,farmaco);
        seriesData[0].setName(seriesName1);
        seriesData[1].setName(seriesName2);
    }

    public ChartDataInstance(int idTerapia,String farmaco,int textId1, int textId2) {
        ChartDataInstance(idTerapia,farmaco);
        setSeriesNameByTextId(seriesData[0], textId1);
        setSeriesNameByTextId(seriesData[1], textId2);
    }
    private void ChartDataInstance(int idTerapia,String farmaco){
        seriesData[0] = new XYChart.Series<String, Number>();
        seriesData[1] = new XYChart.Series<String, Number>();
        this.idTerapia = idTerapia;
        this.farmaco = farmaco;
    }

    private void setSeriesNameByTextId(XYChart.Series<String, Number> series, int textId){
        String name = switch (textId) {
            case PRE -> farmaco + PRE_TEXT;
            case POST -> farmaco + POST_TEXT;
            default -> throw new RuntimeException("nome del grafico non valido");
        };
        series.setName(name);
    }

    public XYChart.Series<String, Number> getSeriesDataPre() {
        return seriesData[0];
    }

    public XYChart.Series<String, Number> getSeriesDataPost() {
        return seriesData[1];
    }
    public ArrayList<XYChart.Series<String, Number>> getSeriesDataList() {
        ArrayList<XYChart.Series<String, Number>> array = new ArrayList<>();
        array.add(seriesData[0]);
        array.add(seriesData[1]);
        return array;
    }


    public int getIdTerapia() {
        return idTerapia;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public void addSeriesData(ArrayList<Rilevazioni> rilevazioni) {
       for (Rilevazioni r : rilevazioni) {
           addDataEntry(r);
       }
    }

    public void addSeriesData(Rilevazioni rilevazioni){
        addDataEntry(rilevazioni);
    }

    private void addDataEntry(Rilevazioni r) {
        if(r != null) {
            String date = r.getDate();
            newChartItem(seriesData[0],date,r.getRilevazionePrePasto());
            newChartItem(seriesData[1],date,r.getRilevazionePostPasto());
        }
    }

    private void newChartItem(XYChart.Series<String, Number> series, String param1, float param2 ) {
        series.getData().add(new XYChart.Data<>(param1, param2));
    }
}
