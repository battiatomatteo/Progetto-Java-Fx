package DAO;

import javafx.scene.control.Alert;
import models.Pasto;
import utility.UIUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import models.Day;

public class PatientPageDao extends DBConnection{
    private Map<String, Pasto> rilevati = new HashMap<>();

    public PatientPageDao(){
        super();
    }

    public Map<String, Pasto> somministrazioneTabella(){
        int idTerapia = 0;
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione == ? AND ID_terapia == ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, UIUtils.dataOggi());
            pstmt.setInt(2, idTerapia);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                String orario = rs.getString("orario");
                float pre = rs.getFloat("rilevazione_pre_pasto");
                float post = rs.getFloat("rilevazione_post_pasto");

                String nomePasto = switch (orario) {
                    case "08:00" -> "Colazione";
                    case "13:00" -> "Pranzo";
                    case "19:30" -> "Cena";
                    default -> "Pasto";
                };

                Pasto pasto = new Pasto(nomePasto, orario, pre, post);
                rilevati.put(orario, pasto);

            }
            return rilevati;
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il caricamento delle somministrazioni.");
            return null;
        }
    }


    public boolean addSomministrazione(Day rilevazioneGiornaliera){
        String sqlInsert = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, ID_terapia, rilevazione_pre_pasto, orario) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)){

            boolean almenoUnoInserito = false;

            String data = rilevazioneGiornaliera.getDataString();
            for (Pasto r : rilevazioneGiornaliera.getPasti()) {
                // Inserisci solo se non esiste già
                insertStmt.setString(1, data);
                insertStmt.setFloat(2, r.getPost());
                insertStmt.setString(3, "note...");
                insertStmt.setInt(4, 2);
                insertStmt.setFloat(5, r.getPre());
                insertStmt.setString(6, r.getOrario());
                insertStmt.executeUpdate();

                almenoUnoInserito = true;
            }
            return  almenoUnoInserito;

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio delle somministrazioni.");
            return false;
        }
    }

    public boolean checkSomministarazione(String orario, LocalDate data, DateTimeFormatter formatter){
        String sqlCheck = "SELECT COUNT(*) FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND orario = ?";
        // Verifica se esiste già una rilevazione per oggi con questo orario
        try(PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)){
            checkStmt.setString(1, data.format(formatter));
            checkStmt.setString(2, orario);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0){
                 return false;
            }
            return true;
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio delle somministrazioni.");
            return false;
        }
    }
}
