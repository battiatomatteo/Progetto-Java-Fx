package models;

import enums.StatoTerapia;
import utility.UIUtils;

import java.util.ArrayList;

public class FilterDataSetter {
    private static final String FARMACO = " farmaco = \'";
    private static final String STATO = " stato = \'";
    private static final String END = "\' ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";

    public static final String ALL_THERAPY = null;

    // Costanti per le visualizzazioni
    public static final int ON_GOING_STATUS = 1;
    public static final int ON_PAUSE_STATUS = 2;
    public static final int ON_GOING_PAUSED_STATUS = 3;
    public static final int TERMINATED_STATUS = 4;
    public static final int ON_GOING_TERMINATED_STATUS = 5;
    public static final int ON_PAUSE_TERMINATED_STATUS = 6;
    public static final int ALL_STATUS_VIEWS = 7;
    public static final int DEFAULT_STATUS_VIEWS = ALL_STATUS_VIEWS;

    private final String patientUserName;
    private final int statusView;
    private final String farmaco;
    private String sqlView;

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
        switch (statusView) {
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

    public String getPatientUserName() {
        return patientUserName;
    }

    public int getStatusView() {
        return statusView;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public String getStatoSqlView() {
        return sqlView;
    }

    public String getFarmacoSqlView() {
        return farmaco == null ? "" : AND + FARMACO + farmaco + END;
    }

    public String getSqlView() {
        if (farmaco == null) {
            return sqlView;
        } else {
            return sqlView + getFarmacoSqlView();
        }
    }

    public static int getViewKey(String stato) {
        return switch (stato) {
            case "ATTIVA" -> ON_GOING_STATUS;
            case "SOSPESA" -> ON_PAUSE_STATUS;
            case "TERMINATA" -> TERMINATED_STATUS;
            default -> 0;
        };
    }

    public static int getViewKey(StatoTerapia stato) {
        return switch (stato) {
            case ATTIVA -> ON_GOING_STATUS;
            case SOSPESA -> ON_PAUSE_STATUS;
            case TERMINATA -> TERMINATED_STATUS;
        };
    }
}
