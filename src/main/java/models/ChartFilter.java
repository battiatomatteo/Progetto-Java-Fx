package models;

import utility.UIUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ChartFilter {
    public static final String NO_START_DATE = null;
    public static final String NO_END_DATE = null;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int NO_ID = ChartDataInstance.MAX_ID;

    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private String startDate;
    private String endDate;
    private final int seriesID;

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
        if(seriesID < 0 || seriesID > ChartDataInstance.MAX_ID){
            this.seriesID = NO_ID;
        }
        else this.seriesID = seriesID;


    }
    public String getSqlStartDataRilevazioneView(){
        if (startDate == null ){
            return "";
        }
        else {
            return " AND  data_rilevazione >= " + '\'' + startDate + '\'' ;
        }
    }
    public String getSqlEndDataRilevazioneView(){
        if (endDate == null){
            return "";
        }
        else {
            return " AND  data_rilevazione <= "+ '\'' + endDate + '\'';
        }
    }
    public String getSqlView(){
        return getSqlStartDataRilevazioneView() + getSqlEndDataRilevazioneView();
    }

    public int getSeriesID() {
        return seriesID;
    }

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
    public boolean isInRange(String dateToCheck) {
        try {
            // Se la data da verificare è null o vuota, ritorniamo false
            if (dateToCheck == null || dateToCheck.isEmpty()) {
                return false;
            }

            // Parso la data da verificare
            java.util.Date date = dateFormat.parse(dateToCheck);

            // Se startDate non è nullo, verifico se la data è dopo la startDate
            if (startDate != null && !startDate.isEmpty()) {
                java.util.Date start = dateFormat.parse(startDate);
                if (date.before(start)) {
                    return false; // La data è prima di startDate
                }
            }

            // Se endDate non è nullo, verifico se la data è prima della endDate
            if (endDate != null && !endDate.isEmpty()) {
                java.util.Date end = dateFormat.parse(endDate);
                if (date.after(end)) {
                    return false; // La data è dopo endDate
                }
            }

            // Se la data è compresa tra startDate ed endDate (inclusi), ritorna true
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // In caso di errore nel parsing
        }
    }

}
