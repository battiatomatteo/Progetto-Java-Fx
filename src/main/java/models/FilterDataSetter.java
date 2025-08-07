package models;

import utility.UIUtils;

import java.util.ArrayList;

public class FilterDataSetter {
    private static final String FARMACO = " farmaco = \'";
    private static final String STATO = " stato = \'";
    private static final String END = "\' ";
    private static final String AND = " AND ";

    public static final String ALL = null;

    private final ChartDataSetter setter;
    private final String farmaco;

    public FilterDataSetter(String patientName, int view,  String farmaco) {
        this.setter = new ChartDataSetter(patientName, view);
        ArrayList<String> farmaciPaziente = UIUtils.getFarmaciPaziente(setter.getPatientUserName());
        if(!farmaciPaziente.contains(farmaco)){
            this.farmaco = ALL;
        }
        else this.farmaco = farmaco;
    }

    public FilterDataSetter(ChartDataSetter setter, String farmaco ) {
        this.setter = setter;
        ArrayList<String> farmaciPaziente = UIUtils.getFarmaciPaziente(setter.getPatientUserName());
        if(!farmaciPaziente.contains(farmaco)){
            this.farmaco = null;
        }
        else this.farmaco = farmaco;
    }

    public String getPatientUserName() {
        return setter.getPatientUserName();
    }

    public int getStatusView() {
        return setter.getView();
    }
    public String getFarmaco() {
        return farmaco;
    }
    public String getStatoSqlView(){
        return setter.getSqlView();
    }
    public String getFarmacoSqlView(){
        return farmaco == null ? "" : AND +  FARMACO + farmaco + END;
    }

    public String getSqlView() {
        if(farmaco == null) return setter.getSqlView();
        else return getStatoSqlView() + getFarmacoSqlView() ;
    }

    public ChartDataSetter getChartDataSetter(){
        return setter;
    }
}
