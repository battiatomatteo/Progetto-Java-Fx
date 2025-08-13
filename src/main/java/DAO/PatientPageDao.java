package DAO;

import javafx.scene.control.Alert;
import models.ChartFilter;
import models.Pasto;
import utility.UIUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import models.Day;

public class PatientPageDao extends DBConnection{
    private Map<String, Pasto> rilevati = new HashMap<>();

    public PatientPageDao(){
        super();
    }

    /*private int recuperoId(String username){
        String sql = "SELECT ID_terapia FROM terapie WHERE username = ? AND stato = 'ATTIVA'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                int lastId = rs.getInt("ID_terapia");
                System.out.println("Ultimo ID: " + lastId);
                return lastId;
            }
            else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }*/

    public Map<String, Pasto> somministrazioneTabella(String username){
        //int idTerapia = recuperoId(username);
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND rilevazioni_giornaliere.username = ?";
        //String sql = "SELECT * FROM rilevazioni_giornaliere INNER JOIN terapie ON (rilevazioni_giornaliere.ID_terapia = terapie.ID_terapia) WHERE data_rilevazione = ? AND rilevazioni_giornaliere.ID_terapia = ? AND username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, UIUtils.dataOggi());
            pstmt.setString(2, username);

            UIUtils.printMessage("query rilevazioni " + sql + "   " + pstmt);
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


    public boolean addSomministrazione(Day rilevazioneGiornaliera, String username){
        String sqlInsert = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, username, rilevazione_pre_pasto, orario) VALUES (?, ?, ?, ?, ?, ?)";
       // int idTerapia = recuperoId(username);
        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)){

            boolean almenoUnoInserito = false;

            String data = rilevazioneGiornaliera.getDataString();
            for (Pasto r : rilevazioneGiornaliera.getPasti()) {
                // Inserisci solo se non esiste già
                insertStmt.setString(1, data);
                insertStmt.setFloat(2, r.getPost());
                insertStmt.setString(3, "note...");
                insertStmt.setString(4, username);
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

    public boolean checkSomministarazione(String orario, LocalDate data, DateTimeFormatter formatter, String username){
        //int idTerapia = recuperoId(username);
        String sqlCheck = "SELECT COUNT(*) FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND orario = ? AND username = ?";
        // Verifica se esiste già una rilevazione per oggi con questo orario
        try(PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)){
            checkStmt.setString(1, data.format(formatter));
            checkStmt.setString(2, orario);
            checkStmt.setString(3, username);
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

    public void messageSomm(String content, String receiver, String user){
        String sql = "INSERT INTO messages (sender, receiver, content, timestamp) VALUES (?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, receiver);
            stmt.setString(3, content);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean recuperoNotifica(String paziente){
        String sql = "SELECT sender FROM messages WHERE receiver = ? AND visualizzato = false GROUP BY sender";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paziente);
            ResultSet rs = pstmt.executeQuery() ;
            return rs.next();
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore recupero notifiche", "Errore nel recupero delle notifiche .");
            return false;
        }
    }

    public void cambioVisualizzato(String doctor, String patient) {
        String update = "UPDATE messages SET visualizzato = true WHERE sender = ? AND receiver = ? AND visualizzato = false ";
        try (PreparedStatement pstmt = conn.prepareStatement(update)) {
            pstmt.setString(1, doctor);
            pstmt.setString(2, patient);
            pstmt.executeUpdate();
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore sql visual.", "Errore nel cambio o recupero stato visualizzato messaggi .");
        }
    }

    /*
    * data1Trovata: diventa true se nel DB troviamo data1.
    * data2Trovata: diventa true se troviamo data2.
    * */
    public boolean messageSommDim(String data2, String data1, ChartFilter filter, String user) {
        String sql = "SELECT data_rilevazione ,  count(data_rilevazione) FROM rilevazioni_giornaliere WHERE username = ? " + filter.getSqlView() + " GROUP BY data_rilevazione";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user);
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0 ;
                while (rs.next()) {
                    String dataRilevazione = rs.getString("data_rilevazione");
                    System.out.println("dataRilevazione " + dataRilevazione);

                    if(dataRilevazione != null) count ++;
                }
                if(count != 0)
                    return false ;
                else
                    return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean messDuplicato(String content, String receiver, String user) {
        UIUtils.printMessage("entro nella funzione");
        String sql = "SELECT content ,  count(content) FROM messages WHERE sender = ? AND receiver = ? AND content = ? GROUP BY content ";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, receiver);
            stmt.setString(3, content);
            try (ResultSet rs = stmt.executeQuery()) {
                // mettere if(rs.next())   return true  perchè basta che ne trova uno
                while(rs.next()) {
                    String contentDb = rs.getString("content");
                    System.out.println(contentDb);
                    if(contentDb.equals(content)){
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore nel cercare il messaggio da confrontare .");
            return false;
        }
    }

}
