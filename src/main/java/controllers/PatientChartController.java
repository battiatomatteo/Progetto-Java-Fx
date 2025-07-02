package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientChartController {

    @FXML
    private LineChart<String, Number> PatientChart;  // Il grafico a linee
    @FXML
    private CategoryAxis xAxis;                      // Asse X (Categoria)
    @FXML
    private NumberAxis yAxis;                        // Asse Y (Numerico)

    @FXML
    public void initialize() {
        // Imposta il titolo del grafico
        PatientChart.setTitle("Evoluzione dei Dati del Paziente");
        setChartData("p");
    }

    private void setChartData(String username) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT ID_terapia, farmaco FROM terapie WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int idTerapia = rs.getInt("ID_terapia");
                String farmaco = rs.getString("farmaco");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(farmaco);
                setChartSeries(series, username, idTerapia);
                PatientChart.getData().add(series);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setChartSeries(XYChart.Series<String, Number> series , String username , int idTerapia) {
        String url = "jdbc:sqlite:miodatabase.db";
        String sql = "SELECT data_rilevazione, rilevazione_post_pasto " +
                "FROM (rilevazioni_giornaliere INNER JOIN terapie ON rilevazioni_giornaliere.ID_terapia = terapie.ID_terapia )" +
                "WHERE username = ? AND terapie.ID_terapia = ?";
            try (Connection conn = DriverManager.getConnection(url)) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, String.valueOf(idTerapia));
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){
                    String date = rs.getString("data_rilevazione");
                    int mensuration = rs.getInt("rilevazione_post_pasto");
                    newChartItem(series, date, mensuration);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void newChartItem(XYChart.Series<String, Number> series, String param1, int param2 ) {
        series.getData().add(new XYChart.Data<>(param1, param2));
    }

}