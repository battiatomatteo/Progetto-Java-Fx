package DAO;

import models.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Classe che gestisce l'accesso al database per la pagina Chat
 * @package DAO
 */
public class MessageDao {
    /**
     * Questo metodo ha lo scopo di salvare il messaggio inviato nel database
     * @param msg Messaggio da salvare
     * @see models.Message
     */
    public void saveMessage(Message msg){
        String sql = "INSERT INTO messages (sender, receiver, content, timestamp) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, msg.getSender());
            stmt.setString(2, msg.getReceiver());
            stmt.setString(3, msg.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(msg.getTimestamp()));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Questo metodo ha lo scopo di recuperare l'intera conversazione tra 2 utenti
     * @param user1
     * @param user2
     * @return List<Message> - la lista ordinata di tutti i messaggi inviati tra i 2 utenti
     * @throws SQLException
     * @see models.Message
     */
    public List<Message> getConversation(String user1, String user2) throws SQLException {
        String sql = "SELECT * FROM messages WHERE " +
                "(sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                "ORDER BY timestamp ASC";
        try  (Connection conn = DBConnection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql))  {
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();

            while (rs.next()) {
                messages.add(new Message(
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
            return messages;

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Questo metodo ha lo scopo di salvare al database una lista di messaggi
     * @param list La lista dei messaggi da inviare
     * @see models.Message
     */
    public void messageList(ArrayList<Message> list ){
        list.forEach(this::saveMessage);
    }

}

