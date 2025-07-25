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

    private final String patientUserName;
    private boolean seeOnGoing;
    private boolean seeOnPause;
    private boolean seeTerminated;
    private String sqlView;

    public ChartDataSetter(String patientUserName, int view) {
        if(view < 1 || view > 7){
            throw  new RuntimeException();
        }
        this.patientUserName = patientUserName;
        switch(view){
            case ON_GOING :ChartDataSetterOnGoing();break;
            case ON_PAUSE :ChartDataSetterOnPause();break;
            case ON_GOING_PAUSED : ChartDataSetterTerminated(); break;
            case TERMINATED : ChartDataSetterOnGoingPaused(); break;
            case ON_GOING_TERMINATED : ChartDataSetterOnGoingTerminated(); break;
            case ON_PAUSE_TERMINATED : ChartDataSetterOnPauseTerminated(); break;
            case ALL : ChartDataSetterAll(); break;

        }
    }

    private void ChartDataSetterOnGoing(){
        this.seeOnGoing = true;
        this.seeOnPause = false;
        this.seeTerminated = false;
        this.sqlView = " stato = " + StatoTerapia.ATTIVA.getStato();
    }
    private void ChartDataSetterOnPause(){
        this.seeOnGoing = false;
        this.seeOnPause = true;
        this.seeTerminated = false;
        this.sqlView = " stato = " + StatoTerapia.SOSPESA.getStato();
    }
    private void ChartDataSetterTerminated(){
        this.seeOnGoing = false;
        this.seeOnPause = false;
        this.seeTerminated = true;
        this.sqlView = " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterOnGoingPaused(){
        this.seeOnGoing = true;
        this.seeOnPause = true;
        this.seeTerminated = false;
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.ATTIVA.getStato() + "\'" + " OR " +
                " stato = " + StatoTerapia.SOSPESA.getStato() ;

    }
    private void ChartDataSetterOnGoingTerminated(){
        this.seeOnGoing = true;
        this.seeOnPause = false;
        this.seeTerminated = true;
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.ATTIVA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterOnPauseTerminated(){
        this.seeOnGoing = false;
        this.seeOnPause = true;
        this.seeTerminated = true;
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.SOSPESA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterAll(){
        this.seeOnGoing = true;
        this.seeOnPause = true;
        this.seeTerminated = true;
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.ATTIVA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.SOSPESA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }

    public String getPatientUserName() {
        return patientUserName;
    }
    public String getSqlView(){
        return sqlView;
    }
    public boolean isSeeOnGoing() {
        return seeOnGoing;
    }

    public boolean isSeeOnPause() {
        return seeOnPause;
    }

    public boolean isSeeTerminated() {
        return seeTerminated;
    }


}
