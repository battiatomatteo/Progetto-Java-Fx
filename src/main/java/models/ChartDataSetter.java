package models;

import enums.StatoTerapia;

public class ChartDataSetter {
    public static final int ON_GOING = 1;
    public static final int ON_PAUSE = 2;
    public static final int ON_GOING_PAUSED = 3;
    public static final int TERMINATED = 4;
    public static final int ON_GOING_TERMINATED = 5;
    public static final int ON_PAUSE_TERMINATED = 6;
    public static final int ALL = 7;
    public static final int DEFAULT = ALL;

    private static final String END = "\' ";
    private static final String STATO = " stato = \'";
    private static final String AND = " AND ";
    private static final String OR = " OR ";


    private final String patientUserName;
    private final int view;
    private String sqlView;

    public ChartDataSetter(String patientUserName, int view) {
        if(patientUserName == null){
            throw new NullPointerException("patientUserName is null");
        }
        this.patientUserName = patientUserName;
        if(view < 1 || view > ALL){
            this.view = DEFAULT;
        }
        else this.view = view;
        switch(view){
            case ON_GOING :ChartDataSetterOnGoing();break;
            case ON_PAUSE :ChartDataSetterOnPause();break;
            case ON_GOING_PAUSED : ChartDataSetterOnGoingPaused(); break;
            case TERMINATED : ChartDataSetterTerminated(); break;
            case ON_GOING_TERMINATED : ChartDataSetterOnGoingTerminated(); break;
            case ON_PAUSE_TERMINATED : ChartDataSetterOnPauseTerminated(); break;
            case ALL : ChartDataSetterAll(); break;
            default: ChartDataSetterDefault(); break;
        }
    }

    private void ChartDataSetterOnGoing(){
        this.sqlView = AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END + " ) ";
    }
    private void ChartDataSetterOnPause(){
        this.sqlView = AND + " ( " + STATO + StatoTerapia.SOSPESA.getStato() + END + " ) ";
    }
    private void ChartDataSetterOnGoingPaused(){
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                 OR + STATO + StatoTerapia.SOSPESA.getStato() + END + " ) ";

    }private void ChartDataSetterTerminated(){
        this.sqlView = AND + " ( " + STATO + StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }
    private void ChartDataSetterOnGoingTerminated(){
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                 OR + STATO+ StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }
    private void ChartDataSetterOnPauseTerminated(){
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.SOSPESA.getStato() + END +
                 OR + STATO+ StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }
    private void ChartDataSetterAll(){
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                 OR + STATO+ StatoTerapia.SOSPESA.getStato() + END +
                 OR + STATO+ StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }
    private void ChartDataSetterDefault(){
        ChartDataSetterAll();
    }

    public String getPatientUserName() {
        return patientUserName;
    }
    public String getSqlView(){
        return sqlView;
    }
    public int getView(){
        return view;
    }
    public static int getViewKey(String stato){
        return switch (stato) {
            case "ATTIVA" -> ON_GOING;
            case "SOSPESA" -> ON_PAUSE;
            case "TERMINATA" -> TERMINATED;
            default -> 0;
        };
    }
    public static int getViewKey(StatoTerapia stato){
        return switch (stato) {
            case ATTIVA -> ON_GOING;
            case SOSPESA -> ON_PAUSE;
            case TERMINATA -> TERMINATED;
        };
    }

}
