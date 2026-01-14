import DAO.DBConnection;
import DAO.MessageDao;
import javafx.scene.control.Alert;
import models.Message;
import utility.UIUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Classe che gestisce l'accesso al database per la pagina DoctorPage
 * @package DAO
 */
public class DoctorPageDaoForTest {
    // Attributi della classe
    /**
     * Oggetto per accesso al database
     * @see MessageDao
     */
    private MessageDao msDao = new MessageDao();

    /**
     * Questo metodo restituisce una lista con tutti i pazienti presenti nel database che sono in cura presso un medico
     * @param doctor Dottore dei pazienti
     * @return ArrayList<String> - Lista ordinata di tutti i pazienti
     */
    public ArrayList<String> getAllDoctorPatients(String doctor){
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT username FROM utenti WHERE medico = ?";
        UIUtils.printMessage("sto cercando i pazienti di  " +  doctor);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UIUtils.printMessage("ho trovato " +  rs.getString("username") + " per il dottore ");
                list.add(rs.getString("username"));
            }
            return list;
        } catch (Exception e) {
            UIUtils.printMessage("non ho trovato pazienti ");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Questo metodo ha lo scopo di inviare un messaggio a tutti i pazienti di un medico
     * @param username Username del dottore
     * @param content Messaggio da inviare
     */
    public void sendAllMess(String username, String content){
        ArrayList<String> pazienti = getAllDoctorPatients(username);
        ArrayList<Message> mess = createMessage(username, pazienti, content);
        msDao.messageList(mess);
    }

    /**
     * Questo metodo compone tutti i messaggi da inviare dal medico a tutti i suoi pazienti
     * @param sender Medico che invia i messaggi
     * @param pazienti Lista di tutti i pazienti del medico
     * @param content Messaggio da inviare
     * @return ArrayList<Message> - lista di tutti i messaggi pronti all'invio
     * @see Message
     */
    private ArrayList<Message> createMessage(String sender, ArrayList<String> pazienti, String content){
        ArrayList<Message> mess = new ArrayList<>();
        pazienti.forEach((paziente) -> {
            Message ms = new Message(sender, paziente, content, LocalDateTime.now());
            mess.add(ms);
        });
        return mess;
    }

    /**
     * Questo metodo ha lo scopo di recuperare dal database tutti i messaggi non visualizzati dal medico
     * @param username Utente a cui effettuare il controllo delle notifiche
     * @return ArrayList<String> - la lista delle notifiche
     */
    public ArrayList<String> recuperoNotifica(String username){
        ArrayList<String> mess = new ArrayList<>();
        String sql = "SELECT sender FROM messages WHERE receiver = ? AND visualizzato = false GROUP BY sender";
        // Nuovo messaggio da: paziente
        String notifica = "Nuovo messaggio da : ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery() ;
            while(rs.next()) {
                mess.add(notifica + rs.getString("sender"));
            }
            return mess;
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore recupero notifiche", "Errore nel recupero delle notifiche .");
            return null;
        }
    }

    /**
     * Questo metodo ha lo scopo di
     * @param doctor
     * @return
     */
    public ArrayList<String> getTooManyDaysWithoutLogIn(String doctor) {
        ArrayList<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate nuovaData = LocalDate.now().minusDays(2);

        String sql ="SELECT utenti.username , MAX(data_rilevazione) AS data " +
                "FROM utenti  " +
                "LEFT JOIN rilevazioni_giornaliere ON (rilevazioni_giornaliere.username = utenti.username) " +
                "WHERE medico = ? " +
                "GROUP BY utenti.username " +
                "ORDER BY data_rilevazione DESC" ;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dataRilevazione = rs.getString("data");
                    String username = rs.getString("username");

                    if(dataRilevazione == null) {
                        System.out.println("il paziente " + username + " non ha registrazioni associate ");
                        list.add("il paziente " + username + " non ha registrazioni associate ");
                    }
                    else if(LocalDate.parse(dataRilevazione, formatter).isBefore(nuovaData)) {
                        System.out.println("il paziente " + username + " non registra una rilevazione da " +  dataRilevazione);
                        list.add("il paziente " + username + " non registra una rilevazione da " +  dataRilevazione);
                    }
                    else {
                        System.out.println("il paziente " + username + " non registra una rilevazione da al massimo 3 giorni" );
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}