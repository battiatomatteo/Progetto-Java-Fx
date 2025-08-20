package DAO;

import javafx.scene.control.Alert;
import models.ChartFilter;
import models.Pasto;
import utility.UIUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import models.Day;

public class PatientPageDao {
    private Map<String, Pasto> rilevati = new HashMap<>();


    public Map<String, Pasto> somministrazioneTabella(String username){
        //int idTerapia = recuperoId(username);
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND rilevazioni_giornaliere.username = ?";
        //String sql = "SELECT * FROM rilevazioni_giornaliere INNER JOIN terapie ON (rilevazioni_giornaliere.ID_terapia = terapie.ID_terapia) WHERE data_rilevazione = ? AND rilevazioni_giornaliere.ID_terapia = ? AND username = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

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
        String sql = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, username, rilevazione_pre_pasto, orario) VALUES (?, ?, ?, ?, ?, ?)";
       // int idTerapia = recuperoId(username);
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement insertStmt = conn.prepareStatement(sql)){

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
        String sql = "SELECT COUNT(*) FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND orario = ? AND username = ?";
        // Verifica se esiste già una rilevazione per oggi con questo orario
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(sql)){
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
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

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
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, paziente);
            ResultSet rs = pstmt.executeQuery() ;
            return rs.next();
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore recupero notifiche", "Errore nel recupero delle notifiche .");
            return false;
        }
    }

    public void cambioVisualizzato(String doctor, String patient) {
        String sql = "UPDATE messages SET visualizzato = true WHERE sender = ? AND receiver = ? AND visualizzato = false ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
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

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public boolean cercoSintomiOggi(LocalDate oggi, DateTimeFormatter formatter, String nuovaNota, String username) {
        String sql = "SELECT ID_rilevazioni FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND username = ? ORDER BY ID_rilevazioni DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, oggi.format(formatter));
            stmt.setString(2, username);
            ResultSet rsOggi = stmt.executeQuery();
            if (rsOggi.next()) {
                // Somministrazione trovata per oggi → aggiorna la più recente
                int id = rsOggi.getInt("ID_rilevazioni");
                String update = "UPDATE rilevazioni_giornaliere SET note_rilevazione = ? WHERE ID_rilevazioni = ? ";
                try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                    updateStmt.setString(1, nuovaNota);
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Nota salvata", "Nota salvata sulla somministrazione odierna.");
                    return true;
                } catch (Exception e) {
                    UIUtils.showAlert(Alert.AlertType.ERROR , "Errore 1.1", "Errore salvataggio sintomi .");
                    return false;
                }
            }
            else return false; // Nessuna somministrazione trovata per oggi
        }catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore 1", "Si è verificato un errore durante il salvataggio della nota.");
            return false; // Nessuna somministrazione trovata per oggi
        }
    }

    public void cercoSintomiGiorniPrecedenti(String nuovaNota, String username) {
        String queryUltima = "SELECT ID_rilevazioni, note_rilevazione, data_rilevazione, username FROM rilevazioni_giornaliere WHERE username = ? ORDER BY data_rilevazione DESC, ID_rilevazioni DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmtUltima = conn.prepareStatement(queryUltima)) {
            pstmtUltima.setString(1, username);
            ResultSet rsUltima = pstmtUltima.executeQuery();
            if (rsUltima.next()) {
                String note = rsUltima.getString("note_rilevazione");
                int id = rsUltima.getInt("ID_rilevazioni");
                String dataUltima = rsUltima.getString("data_rilevazione");

                if ("note...".equalsIgnoreCase(note)) {
                    // Aggiorno la nota
                    String update = "UPDATE rilevazioni_giornaliere SET note_rilevazione = ? WHERE ID_rilevazioni = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
                        updateStmt.setString(1, nuovaNota);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Nota salvata", "Nota salvata nella somministrazione più recente del " + dataUltima);
                    } catch (Exception e) {
                        UIUtils.showAlert(Alert.AlertType.ERROR , "Errore 2.1", "Errore salvataggio sintomi .");
                    }
                } else {
                    // Nota già presente → mostro alert
                    UIUtils.showAlert(Alert.AlertType.WARNING, "Nota non salvata", "Hai già scritto una nota nella somministrazione più recente. Contatta il medico o attendi una nuova somministrazione.");
                }
            } else {
                UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna rilevazione", "Non è presente alcuna somministrazione su cui salvare la nota.");
            }
        }catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore 2", "Si è verificato un errore durante il salvataggio della nota.");
        }
    }

}
