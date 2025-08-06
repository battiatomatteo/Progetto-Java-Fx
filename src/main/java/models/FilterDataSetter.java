package models;

public class FilterDataSetter extends ChartDataSetter{
    private final int farmacoView;

    public FilterDataSetter(ChartDataSetter setter, int farmacoView) {
        super(setter.getPatientUserName(),setter.getView());
        if(farmacoView < 1){
            throw  new RuntimeException();
        }
        this.farmacoView = farmacoView;
    }
    public int getStatusView() {
        return super.getView();
    }
    public int getFarmacoView() {
        return farmacoView;
    }
}
