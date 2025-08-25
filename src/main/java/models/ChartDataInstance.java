package models;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;


/**
 * Classe utilizzata per raggruppare e gestire i dati utilizzati dal grafico
 * @package models
 * @see controllers.PatientChartController
 */
public class ChartDataInstance {

    // Costanti definite per la classe
    public static final int MAX_ID = 2;
    public static final int PRE = 0;
    public static final int POST = 1;
    public static final String PRE_TEXT = "Rilevazioni pre pasto";
    public static final String POST_TEXT = "Rilevazioni post pasto";
    public static final String ALL_TEXT = "tutte le rilevazioni";
    public static final String[] TEXT_LIST = {PRE_TEXT,POST_TEXT,ALL_TEXT};

    // Attributi della classe
    /**
     * Lista di tutte le serie che verranno utilizzate
     */
    private final XYChart.Series<String, Number>[] seriesData = new XYChart.Series[MAX_ID];

    // Metodi della classe
    /**
     * Costruttore della Classe
     */
    public ChartDataInstance() {
        seriesData[0] = new XYChart.Series<String, Number>();
        seriesData[1] = new XYChart.Series<String, Number>();
        setSeriesNameByTextId(seriesData[0], PRE);
        setSeriesNameByTextId(seriesData[1], POST);
    }

    /**
     * Questo metodo ha lo scopo di impostare il nome della serie di dati visualizzata dato l'ID della stringa da associare
     * @param series il riferimento della serie
     * @param textId l'ID associato al titolo da assegnare
     */
    private void setSeriesNameByTextId(XYChart.Series<String, Number> series, int textId){
        String name = switch (textId) {
            case PRE -> PRE_TEXT;
            case POST -> POST_TEXT;
            default -> throw new RuntimeException("nome del grafico non valido");
        };
        series.setName(name);
    }

    /**
     * Questo metodo restituisce una serie dato il suo ID
     * @param seriesId L'indice della serie nella lista delle serie
     * @return La serie corrispondente all'ID
     * @throws RuntimeException se l'ID della serie non è valido
     */

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

    /**
     * Questo metodo restituisce tutte le serie
     * @return La lista di tutte le serie della classe
     */
    public ArrayList<XYChart.Series<String, Number>> getSeriesDataList() {
        ArrayList<XYChart.Series<String, Number>> array = new ArrayList<>();
        array.add(seriesData[0]);
        array.add(seriesData[1]);
        return array;
    }


    /**
     * Questo metodo ha lo scopo di aggiungere una lista di rilevazioni alle serie
     * @param rilevazioni La lista di rilevazioni da aggiungere
     */
    public void addSeriesData(ArrayList<Rilevazioni> rilevazioni) {
        for (Rilevazioni r : rilevazioni) {
            addDataEntry(r);
        }
    }

    /**
     * Questo metodo ha lo scopo di aggiunger una sola rilevazione alle serie
     * @param rilevazioni
     */
    public void addSeriesData(Rilevazioni rilevazioni){
        addDataEntry(rilevazioni);
    }

    /**
     * Implementazione dell'inserimento di una rilevazione alle serie
     * @param r Rilevazione da aggiungere
     */
    private void addDataEntry(Rilevazioni r) {
        if(r != null) {
            String date = r.getDate();
            newChartItem(seriesData[0],date,r.getRilevazionePrePasto());
            newChartItem(seriesData[1],date,r.getRilevazionePostPasto());
        }
    }

    /**
     * Questo metodo ha lo scopo di semplificare la sintassi dell'inserimento della rilevazione
     * @param series La serie 
     * @param param1 Primo parametro da aggiungere
     * @param param2 Secondo parametro da aggiungere
     */
    private void newChartItem(XYChart.Series<String, Number> series, String param1, float param2 ) {
        series.getData().add(new XYChart.Data<>(param1, param2));
    }

}
