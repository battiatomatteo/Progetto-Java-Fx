package DAO;

import models.ChartFilter;
import models.Rilevazioni;
import utility.UIUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 * Classe che gestisce l'accesso al database per il grafico
 * @package DAO
 */
public class PatientChartDao {
    /**
     * Questo metodo recupera tutte le rilevazioni di un utente applicando il filtro desiderato
     * @param username Nome del paziente
     * @param filter Filtro da applicare ai dati
     * @return ArrayList<Rilevazioni> - tutte le rilevazioni che rispettano il criterio del filtro
     * @see models.ChartFilter
     */
    public ArrayList<Rilevazioni> getSommRilevati(String username, ChartFilter filter) {
        ArrayList<Rilevazioni> rilevazioni = new ArrayList<>();
        String sql = "SELECT data_rilevazione, rilevazione_post_pasto, rilevazione_pre_pasto, orario " +
                "FROM rilevazioni_giornaliere WHERE username = ? " + filter.getSqlView();
        // UIUtils.printMessage("query " + sql);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            UIUtils.printMessage(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String date =rs.getString("orario") + '/' +
                        rs.getString("data_rilevazione");
                float mensuration_pre = rs.getInt("rilevazione_pre_pasto");
                float mensuration_post = rs.getInt("rilevazione_post_pasto");
                Rilevazioni result = new Rilevazioni(date,mensuration_pre,mensuration_post);
                rilevazioni.add(result);
            }
            return rilevazioni;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Questo metodo recupera tutti i giorni in cui il paziente ha inserito almeno una rilevazione
     * @param username Nome del paziente
     * @return ArrayList<Rilevazioni> - tutte le giornate in cui dono presenti delle rilevazioni per il paziente
     */
    public ArrayList<String> getDateRilevazioni(String username) {
        ArrayList<String> date = new ArrayList<>();
        String sql = "SELECT data_rilevazione FROM rilevazioni_giornaliere WHERE username = ? GROUP BY data_rilevazione";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String data = rs.getString("data_rilevazione");
                date.add(data);
            }
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
