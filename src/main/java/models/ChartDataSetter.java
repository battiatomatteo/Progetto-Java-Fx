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
    private final int view;
    private String sqlView;

    public ChartDataSetter(String patientUserName, int view) {
        if(view < 1 || view > 7){
            throw  new RuntimeException();
        }
        this.patientUserName = patientUserName;
        this.view = view;
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
        this.sqlView = " stato = " + StatoTerapia.ATTIVA.getStato();
    }
    private void ChartDataSetterOnPause(){
        this.sqlView = " stato = " + StatoTerapia.SOSPESA.getStato();
    }
    private void ChartDataSetterTerminated(){
        this.sqlView = " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterOnGoingPaused(){
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.ATTIVA.getStato() + "\'" + " OR " +
                " stato = " + StatoTerapia.SOSPESA.getStato() ;

    }
    private void ChartDataSetterOnGoingTerminated(){
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.ATTIVA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterOnPauseTerminated(){
        this.sqlView =
                " stato = " + "\'"+ StatoTerapia.SOSPESA.getStato() + "\'" + " OR " +
                " stato = " + "\'"+ StatoTerapia.TERMINATA.getStato() + "\'";
    }
    private void ChartDataSetterAll(){
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
    public int getView(){
        return view;
    }

}
