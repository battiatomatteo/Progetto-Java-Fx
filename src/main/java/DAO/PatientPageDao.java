package DAO;

import javafx.scene.control.Alert;
import models.Pasto;
import utility.UIUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;

public class PatientPageDao extends DBConnection{
    public PatientPageDao(){
        super();
    }

    public Map<String, Pasto> somministrazioneTabella(Map<String, Pasto> rilevati){

        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione == ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, UIUtils.dataOggi());
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                String orario = rs.getString("orario");
                String pre = Float.toString(rs.getFloat("rilevazione_pre_pasto"));
                String post = Float.toString(rs.getFloat("rilevazione_post_pasto"));

                String nomePasto = switch (orario) {
                    case "08:00" -> "Colazione";
                    case "13:00" -> "Pranzo";
                    case "19:30" -> "Cena";
                    default -> "Pasto";
                };

                Pasto pasto = new Pasto(nomePasto, orario, pre, post);
                return (Map<String, Pasto>) rilevati.put(orario, pasto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il caricamento delle somministrazioni.");
            return null;
        }
        return null;
    }
}
