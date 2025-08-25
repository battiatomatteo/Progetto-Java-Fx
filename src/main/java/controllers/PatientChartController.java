package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import models.ChartDataInstance;
import java.util.ArrayList;
import DAO.PatientChartDao;
import models.ChartFilter;
import models.Rilevazioni;

/**
 * Classe che gestisce il grafico per visualizzare le rilevazione di un paziente
 * @packege controllers
* @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/PatientChart.fxml">PatientChartr.fxml</a>
 */
public class PatientChartController {
    // Attributi della classe
    /**
     * Grafico a linee
     */
    @FXML private LineChart<String, Number> PatientChart;  // Il grafico a linee
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientChartDao
     */
    private PatientChartDao dao;

    @FXML
    public void initialize() {
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");   // Imposta il titolo del grafico
        dao = new PatientChartDao();
    }

    /**
     * Questo metodo si occupa di aggiornare il grafico svuotandolo e caricando i nuovi dati
     * @param username Username del paziente
     * @param filter Filtro applicato al grafico
     * @see ChartFilter
     */
    public void setData(String username , ChartFilter filter){
        PatientChart.getData().clear();
        setChartData(username, filter);
    }

    /**
     * Questo metodo si occupa di caricare i nuovi dati
     * @param username Username del paziente
     * @param filter Filtro applicato al grafico
     * @see ChartFilter
     */
    private void setChartData(String username, ChartFilter filter) {
        ArrayList<Rilevazioni> rilevazioni = dao.getSommRilevati(username, filter);
        if (rilevazioni != null && !rilevazioni.isEmpty()) {
            ChartDataInstance data = new ChartDataInstance();
            data.addSeriesData(rilevazioni);  // popola i dati
            switch (filter.getSeriesID()){
                case ChartDataInstance.PRE:{
                    PatientChart.getData().add(data.getSeriesById(ChartDataInstance.PRE));
                    return;
                }
                case ChartDataInstance.POST:{
                    PatientChart.getData().add(data.getSeriesById(ChartDataInstance.POST));
                    return;
                }
                case ChartDataInstance.MAX_ID:{
                    PatientChart.getData().addAll(data.getSeriesDataList());
                }
            }

        }
    }
}