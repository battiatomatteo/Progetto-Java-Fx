package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Questa classe crea un oggetto per filtrare gli elementi sulla base di un intervallo di date e una serie di dati
 * @package models
 */
public class ChartFilter {

    // Constanti definite per la classe
    public static final String NO_START_DATE = null;
    public static final String NO_END_DATE = null;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int NO_ID = ChartDataInstance.MAX_ID;
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    // Attributi della classe
    private String startDate;
    private String endDate;
    private final int seriesID;

    /**
     * Costruttore della classe
     * @param startDate Estremo inferiore dell'intervallo
     * @param endDate Estremo superiore dell'ntervallo
     * @param seriesID ID della serie
     * @see ChartDataInstance
     */
    public ChartFilter(String startDate, String endDate, int seriesID) {
        if(isValidDate(startDate)) {
            this.startDate = startDate;
        }
        else{
            this.startDate = NO_START_DATE;
        }

        if(isValidDate(endDate)){
            this.endDate = endDate;
        }
        else{
            this.endDate = NO_END_DATE;
        }

        // Controllo della condizione startDate <= endDate
        if (this.startDate != null && this.endDate != null) {
            try {
                java.util.Date start = dateFormat.parse(this.startDate);
                java.util.Date end = dateFormat.parse(this.endDate);

                if (start.after(end)) {
                    // Scambia le date
                    String temp = this.startDate;
                    this.startDate = this.endDate;
                    this.endDate = temp;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Errore nelle date: " + e.getMessage());
            }
        }
        // Se la serie non ha un id valido viene settata al valore di default neutro
        if(seriesID < 0 || seriesID > ChartDataInstance.MAX_ID){
            this.seriesID = NO_ID;
        }
        else this.seriesID = seriesID;


    }

    /**
     * Questo metodo restituisce la stringa SQL per filtrare solo per data di inizio
     * @return la stringa sql del filtro
     */
    public String getSqlStartDataRilevazioneView(){
        if (startDate == null ){
            return "";
        }
        else {
            return " AND  data_rilevazione >= " + '\'' + startDate + '\'' ;
        }
    }
    /**
     * Questo metodo restituisce la stringa SQL per filtrare solo per data di fine
     * @return la stringa sql del filtro
     */
    public String getSqlEndDataRilevazioneView(){
        if (endDate == null){
            return "";
        }
        else {
            return " AND  data_rilevazione <= "+ '\'' + endDate + '\'';
        }
    }
    /**
     * Questo metodo restituisce la stringa SQL per filtrare per l'intervallo di date del filtro
     * @return la stringa sql del filtro
     */
    public String getSqlView(){
        return getSqlStartDataRilevazioneView() + getSqlEndDataRilevazioneView();
    }

    /**
     * Queesto metodo restituisce l'ID della serie
     * @return L'ID della serie
     */
    public int getSeriesID() {
        return seriesID;
    }

    /**
     * Questa funzione ha lo scopo valutare se la stringa fornita è una data valida
     * @param dateStr Stringa da valutare
     * @return Esito della valutazione
     */
    private boolean isValidDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return false;
        }
        dateFormat.setLenient(false); // Impedisce date non valide come 32/01/2020
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
