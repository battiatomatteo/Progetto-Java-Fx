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
    private final String startDate;
    private final String endDate;
    private final int seriesID;

    public ChartFilter(String startDate, String endDate, int seriesID) {
        UIUtils.printMessage("oggetto Chart filter creato con" + startDate + " " + endDate + " " + seriesID);
        if(isValidDate(startDate)) {
            UIUtils.printMessage(" start date valido");
            this.startDate = startDate;
        }
        else{
            UIUtils.printMessage("start date invalido");
            this.startDate = NO_START_DATE;
        }

        if(isValidDate(endDate)){
            UIUtils.printMessage("end date valido");
            this.endDate = endDate;
        }
        else{
            UIUtils.printMessage("end date invalido");
            this.endDate = NO_END_DATE;
        }

        if(seriesID < 0 || seriesID > ChartDataInstance.MAX_ID){
            this.seriesID = NO_ID;
        }
        else this.seriesID = seriesID;
        UIUtils.printMessage("oggetto Chart filter inizializzato con" + startDate + " " + endDate + " " + seriesID);


    }
    public String getSqlStartDataRilevazioneView(){
        if (startDate == null ){
            UIUtils.printMessage("oggetto Chart filter metodo sql creato vuoto");
            return "";
        }
        else {
            UIUtils.printMessage("oggetto Chart filter metodo sql inizializzato con start date" + startDate);
            return " AND  data_rilevazione >= " + '\'' + startDate + '\'' ;
        }
    }
    public String getSqlEndDataRilevazioneView(){
        if (endDate == null){
            UIUtils.printMessage("oggetto Chart filter metodo sql creato vuoto");
            return "";
        }
        else {
            UIUtils.printMessage("oggetto Chart filter metodo sql inizializzato con"+ endDate);
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
            UIUtils.printMessage("data che non passa controllo 1");
            return false;
        }
        dateFormat.setLenient(false); // Impedisce date non valide come 32/01/2020
        try {
            dateFormat.parse(dateStr);
            UIUtils.printMessage("data ok");
            return true;
        } catch (Exception e) {
            UIUtils.printMessage("data che non passa controllo 2");
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

            // Se la data è compresa tra startDate e endDate (inclusi), ritorna true
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // In caso di errore nel parsing
        }
    }

}
