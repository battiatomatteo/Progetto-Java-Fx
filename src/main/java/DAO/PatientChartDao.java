package DAO;

import enums.StatoTerapia;
import models.ChartDataSetter;
import models.FilterDataSetter;
import models.Rilevazioni;
import utility.UIUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class PatientChartDao extends DBConnection{
    public PatientChartDao() {
        super();
    }

    public HashMap<Integer,String> getTerapiePaziente(FilterDataSetter setter){
        HashMap<Integer,String> rilevati = new HashMap<>();
        String farmaco;
        int idTerapia;
        String sql = "SELECT ID_terapia, farmaco FROM terapie WHERE username = ? " + setter.getSqlView();
        //UIUtils.printMessage(sql);
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, setter.getPatientUserName());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                idTerapia =  rs.getInt("ID_terapia");
                farmaco = rs.getString("farmaco");
                rilevati.put(idTerapia,farmaco);
            }
            return rilevati;

        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Rilevazioni> getSommRilevati(String username, int idTerapia){
        ArrayList<Rilevazioni> rilevazioni = new ArrayList<>();
        String sql = "SELECT data_rilevazione, rilevazione_post_pasto, rilevazione_pre_pasto " +
                "FROM (rilevazioni_giornaliere INNER JOIN terapie ON rilevazioni_giornaliere.ID_terapia = terapie.ID_terapia )" +
                "WHERE username = ? AND terapie.ID_terapia = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, idTerapia);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String date = rs.getString("data_rilevazione");
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


}
