package DAO;

import javafx.scene.control.Alert;
import models.Message;
import utility.UIUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DoctorPageDao {
    private MessageDao msDao = new MessageDao();

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

    public void sendAllMess(String username, String content){
        System.out.println("sono in sendAllMess in dao");
        ArrayList<String> pazienti = getAllDoctorPatients(username);
        ArrayList<Message> mess = createMessage(username, pazienti, content);
        msDao.messageList(mess);
    }

    private ArrayList<Message> createMessage(String sender, ArrayList<String> pazienti, String content){
        ArrayList<Message> mess = new ArrayList<>();
        pazienti.forEach((paziente) -> {
            UIUtils.printMessage("messaggio creato per " + paziente);
            Message ms = new Message(sender, paziente, content, LocalDateTime.now());
            mess.add(ms);
        });
        return mess;
    }

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
}