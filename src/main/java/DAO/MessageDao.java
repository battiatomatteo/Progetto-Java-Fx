package DAO;

import models.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao extends DBConnection {

    public MessageDao() {
        super();
    }

    public void saveMessage(Message msg){
        String sql = "INSERT INTO messages (sender, receiver, content, timestamp) VALUES (?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, msg.getSender());
            stmt.setString(2, msg.getReceiver());
            stmt.setString(3, msg.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(msg.getTimestamp()));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> getConversation(String user1, String user2) throws SQLException {
        String sql = "SELECT * FROM messages WHERE " +
                "(sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                "ORDER BY timestamp ASC";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
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

    public void messageList(ArrayList<Message> list ){
        list.forEach(this::saveMessage);
    }

}

