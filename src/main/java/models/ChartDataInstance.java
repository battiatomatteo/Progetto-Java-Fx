package models;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class ChartDataInstance {
    private final XYChart.Series<String, Number>[] seriesData = new XYChart.Series[MAX_ID];
    public static final int MAX_ID = 2;
    public static final int PRE = 0;
    public static final int POST = 1;
    public static final String PRE_TEXT = "Rilevazioni pre pasto";
    public static final String POST_TEXT = "Rilevazioni post pasto";
    public static final String ALL_TEXT = "tutte le rilevazioni";
    public static final String[] TEXT_LIST = {PRE_TEXT,POST_TEXT,ALL_TEXT};

    public ChartDataInstance(String seriesName1 , String seriesName2) {
        ChartDataInstanceInit();
        seriesData[0].setName(seriesName1);
        seriesData[1].setName(seriesName2);
    }

    public ChartDataInstance() {
        ChartDataInstanceInit();
        setSeriesNameByTextId(seriesData[0], PRE);
        setSeriesNameByTextId(seriesData[1], POST);
    }
    private void ChartDataInstanceInit(){
        seriesData[0] = new XYChart.Series<String, Number>();
        seriesData[1] = new XYChart.Series<String, Number>();
    }

    private void setSeriesNameByTextId(XYChart.Series<String, Number> series, int textId){
        String name = switch (textId) {
            case PRE -> PRE_TEXT;
            case POST -> POST_TEXT;
            default -> throw new RuntimeException("nome del grafico non valido");
        };
        series.setName(name);
    }

    public XYChart.Series<String, Number> getSeriesById(int seriesId) {
        switch (seriesId) {
            case PRE -> {
                return seriesData[PRE];
            }
            case POST -> {
                return  seriesData[POST];
            }
            default -> throw new RuntimeException("id serie non valido");
        }
    }

    public ArrayList<XYChart.Series<String, Number>> getSeriesDataList() {
        ArrayList<XYChart.Series<String, Number>> array = new ArrayList<>();
        array.add(seriesData[0]);
        array.add(seriesData[1]);
        return array;
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
