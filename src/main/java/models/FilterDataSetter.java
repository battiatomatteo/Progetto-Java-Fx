package models;

import enums.StatoTerapia;
import utility.UIUtils;

import java.util.ArrayList;

public class FilterDataSetter {

    // Costanti definite per la classe
        // Costanti per semplificare per la formazione delle strighe SQL
    private static final String FARMACO = " farmaco = \'";
    private static final String STATO = " stato = \'";
    private static final String END = "\' ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";

        // Costanti per la visualizzazione della terapia
    public static final String ALL_THERAPY = null;

        // Costanti per la visualizzazione dello stato
    public static final int ON_GOING_STATUS = 1;
    public static final int ON_PAUSE_STATUS = 2;
    public static final int ON_GOING_PAUSED_STATUS = 3;
    public static final int TERMINATED_STATUS = 4;
    public static final int ON_GOING_TERMINATED_STATUS = 5;
    public static final int ON_PAUSE_TERMINATED_STATUS = 6;
    public static final int ALL_STATUS_VIEWS = 7;
    public static final int DEFAULT_STATUS_VIEWS = ALL_STATUS_VIEWS;

    // Attributi della classe
    private final String patientUserName;
    private final int statusView;
    private final String farmaco;
    private String sqlView;

    /**
     * Costruttore della classe
     * @param patientName Nome del paziente
     * @param statusView Vista da applicare allo stato
     * @param farmaco Nome del farmaco
     */
    public FilterDataSetter(String patientName, int statusView, String farmaco) {
        if (patientName == null) {
            throw new NullPointerException("patientUserName is null");
        }
        this.patientUserName = patientName;

        // Imposta la vista
        if (statusView < 1 || statusView > ALL_STATUS_VIEWS) {
            this.statusView = DEFAULT_STATUS_VIEWS;
        } else {
            this.statusView = statusView;
        }

        // Imposta il farmaco
        ArrayList<String> farmaciPaziente = UIUtils.getFarmaciPaziente(patientName);
        if (!farmaciPaziente.contains(farmaco)) {
            this.farmaco = ALL_THERAPY;
        } else {
            this.farmaco = farmaco;
        }

        // Costruisci la query SQL in base alla vista
        switch (this.statusView) {
            case ON_GOING_STATUS:
                chartDataSetterOnGoing();
                break;
            case ON_PAUSE_STATUS:
                chartDataSetterOnPause();
                break;
            case ON_GOING_PAUSED_STATUS:
                chartDataSetterOnGoingPaused();
                break;
            case TERMINATED_STATUS:
                chartDataSetterTerminated();
                break;
            case ON_GOING_TERMINATED_STATUS:
                chartDataSetterOnGoingTerminated();
                break;
            case ON_PAUSE_TERMINATED_STATUS:
                chartDataSetterOnPauseTerminated();
                break;
            case ALL_STATUS_VIEWS:
                chartDataSetterAll();
                break;
            default:
                chartDataSetterDefault();
                break;
        }
    }

    // Metodi per gestire le varie condizioni di stato

    private void chartDataSetterOnGoing() {
        this.sqlView = AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END + " ) ";
    }

    private void chartDataSetterOnPause() {
        this.sqlView = AND + " ( " + STATO + StatoTerapia.SOSPESA.getStato() + END + " ) ";
    }

    private void chartDataSetterOnGoingPaused() {
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                        OR + STATO + StatoTerapia.SOSPESA.getStato() + END + " ) ";
    }

    private void chartDataSetterTerminated() {
        this.sqlView = AND + " ( " + STATO + StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }

    private void chartDataSetterOnGoingTerminated() {
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                        OR + STATO + StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }

    private void chartDataSetterOnPauseTerminated() {
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.SOSPESA.getStato() + END +
                        OR + STATO + StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }

    private void chartDataSetterAll() {
        this.sqlView =
                AND + " ( " + STATO + StatoTerapia.ATTIVA.getStato() + END +
                        OR + STATO + StatoTerapia.SOSPESA.getStato() + END +
                        OR + STATO + StatoTerapia.TERMINATA.getStato() + END + " ) ";
    }

    private void chartDataSetterDefault() {
        chartDataSetterAll();
    }

    /**
     * Questo metodo restituisce l'username del paziente
     * @return il nome del paziente
     */
    public String getPatientUserName() {
        return patientUserName;
    }

    /**
     * Questo metodo restituisce la stringa SQL per filtrare per farmaco
     * @return la stringa SQL del filtro
     */
    public String getFarmacoSqlView() {
        return farmaco == null ? "" : AND + FARMACO + farmaco + END;
    }

    /**
     * Questo metodo restituisce la stringa SQL per filtrare pre farmaco e stato
     * @return la stringa SQL del filtro
     */
    public String getSqlView() {
        if (farmaco == null) {
            return sqlView;
        } else {
            return sqlView + getFarmacoSqlView();
        }
    }
}
