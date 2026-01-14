import DAO.DBConnection;
import javafx.scene.control.Alert;
import models.ChartFilter;
import models.Day;
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

/**
 * Classe che gestisce l'accesso al database per i metodi presenti in PatientPage
 * @package DAO
 */
public class PatientPageDaoForTest {

    /**
     * Metodo con lo scopo di ottenere le somministrazioni da mostrare nella tabella odierna
     * @param username nome paziente
     * @return Map<String, Pasto> - le rilevazioni del giorno corrente mappate per orario
     * @see Pasto
     */
    public Map<String, Pasto> somministrazioneTabella(String username){
        // rilevazioni del giorno corrente mappate per orario
        Map<String, Pasto> rilevati = new HashMap<>();
        String sql = "SELECT * FROM rilevazioni_giornaliere WHERE data_rilevazione = ? AND rilevazioni_giornaliere.username = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, UIUtils.dataOggi());
            pstmt.setString(2, username);

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
            System.out.println( "Errore durante il caricamento delle somministrazioni.");
            return null;
        }
    }

    /**
     * Metodo con los scopo di aggiungere le nuove somministrazioni nel database
     * @param rilevazioneGiornaliera
     * @param username nome paziente
     * @return boolean - valore booleano, true se ci sta almeno un inseriemento, false altrimenti
     * @see Day
     */
    public boolean addSomministrazione(Day rilevazioneGiornaliera, String username){
        String sql = "INSERT INTO rilevazioni_giornaliere (data_rilevazione, rilevazione_post_pasto, note_rilevazione, username, rilevazione_pre_pasto, orario) VALUES (?, ?, ?, ?, ?, ?)";

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
            System.out.println( "Errore durante il salvataggio delle somministrazioni.");
            return false;
        }
    }

    /**
     * Metodo con lo scopo di controllare le somministrazioni, se esiste già una rilevazione per oggi con questo orario
     * @param orario orario somministrazione
     * @param data data odierna
     * @param formatter data formattata
     * @param username nome paziente
     * @return boolean - valore booleano, true se ci sta la somministrazione, false altrimenti
     */
    public boolean checkSomministarazione(String orario, LocalDate data, DateTimeFormatter formatter, String username){

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
            System.out.println( "Errore durante il salvataggio delle somministrazioni.");
            return false;
        }
    }

    /**
     * Metodo con lo scopo di inserire nel database il messaggio della mancata somministrazione inviato o nel caso in cui le
     * somministrazioni sono fuori dal range minimo e massimo, messaggio automatico
     * @param content contenuto massaggio
     * @param receiver chi riceve
     * @param user chi invia
     */
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

    /**
     * Metodo con lo scopo di recuperare la notifica
     * @param paziente
     * @return boolean - valore booleano, true nel caso ci sia almeno una notifica, false altrimenti
     */
    public boolean recuperoNotifica(String paziente){
        String sql = "SELECT sender FROM messages WHERE receiver = ? AND visualizzato = false GROUP BY sender";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, paziente);
            ResultSet rs = pstmt.executeQuery() ;
            return rs.next();
        } catch (Exception e) {
            System.out.println( "Errore recupero notifiche"+ "Errore nel recupero delle notifiche .");
            return false;
        }
    }

    /**
     * Metodo con lo scopo di aggiornare lo stato della visuallizzazione del messaggio
     * @param doctor medico
     * @param patient paziente
     */
    public void cambioVisualizzato(String doctor, String patient) {
        String sql = "UPDATE messages SET visualizzato = true WHERE sender = ? AND receiver = ? AND visualizzato = false ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, doctor);
            pstmt.setString(2, patient);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println( "Errore sql visual."+ "Errore nel cambio o recupero stato visualizzato messaggi .");
        }
    }

    /**
     * Questo metodo ha lo scopo di salvare i sintomi inseriti dal paziente
     * @param nuovaNota - nota da salvare
     * @param username - username paziente
     * @param oggi - data odierna
     * @param formatter - data formattata
     * @return boolean - valore booleano , true che è andato a buon fine, false altrimenti
     */
    public boolean cercoSintomi(String nuovaNota, String username, LocalDate oggi, DateTimeFormatter formatter) {
        // Query per trovare la rilevazione più recente per oggi, se presente, altrimenti la più recente in assoluto
        String sql = "SELECT ID_rilevazioni, note_rilevazione, data_rilevazione " +
                "FROM rilevazioni_giornaliere " +
                "WHERE username = ? AND (data_rilevazione = ? OR data_rilevazione <= ?) " +
                "ORDER BY data_rilevazione DESC, ID_rilevazioni DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, oggi.format(formatter));  // Filtra per oggi
            stmt.setString(3, oggi.format(formatter));  // Altrimenti, trova la più recente in assoluto

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("ID_rilevazioni");
                String note = rs.getString("note_rilevazione");

                // Se la nota è già presente (per esempio "note..."), aggiorniamo
                if ("note...".equalsIgnoreCase(note) || note == null) {
                    return aggiornaNota(conn, nuovaNota, id);
                } else {
                    // La nota esiste già, quindi mostriamo un avviso
                    System.out.println("Nota non salvata " + "Hai già scritto una nota nella rilevazione più recente.");
                    return false;
                }
            } else {
                System.out.println( "Nessuna rilevazione " + "Non è presente alcuna rilevazione su cui salvare la nota.");
                return false;
            }
        } catch (Exception e) {
            gestisciErrore("Errore durante la ricerca dei sintomi", e);
            return false;
        }
    }

    /**
     * Questo metodo ha lo scopo di aggiornare i sintomi del paziente nel database
     * @param conn - connessione
     * @param nuovaNota - nuova nota da inserire
     * @param id - id rilevazioni
     * @return boolean - valore booleano, true nel caso il sintomo è stato aggiornato, false altrimenti
     */
    private boolean aggiornaNota(Connection conn, String nuovaNota, int id) {
        String update = "UPDATE rilevazioni_giornaliere SET note_rilevazione = ? WHERE ID_rilevazioni = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(update)) {
            updateStmt.setString(1, nuovaNota);
            updateStmt.setInt(2, id);
            updateStmt.executeUpdate();
            System.out.println( "Nota salvata " + "Nota aggiornata con successo.");
            return true;
        } catch (Exception e) {
            gestisciErrore("Errore durante l'aggiornamento della nota", e);
            return false;
        }
    }

    /**
     * Questo metodo ha lo scopo di gestire gli errori
     * @param messaggio - messaggio errore
     * @param e
     */
    private void gestisciErrore(String messaggio, Exception e) {
        e.printStackTrace();  // Puoi loggare anche l'errore per debug
        System.out.println( messaggio + " Dettagli: " + e.getMessage());
    }

}
