package controllers;

import DAO.MessageDao;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.Message;

import java.time.LocalDateTime;
import java.util.List;

public class ChatController {

    @FXML private VBox messageContainer;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;

    private MessageDao messageDao = new MessageDao();

    private String currentUser;
    private String chattingWith;

    public void initializeChat(String currentUser, String chattingWith) {
        this.currentUser = currentUser;
        this.chattingWith = chattingWith;
        loadMessages();
    }

    @FXML
    public void initialize() {   // Auto-Aggiornamento della Chat
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> loadMessages()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private void loadMessages() {
        messageContainer.getChildren().clear();
        try {
            List<Message> messages = messageDao.getConversation(currentUser, chattingWith);
            for (Message msg : messages) {
                Text text = new Text(msg.getSender() + ": " + msg.getContent());
                messageContainer.getChildren().add(text);
            }
            chatScrollPane.setVvalue(1.0); // Scroll to bottom
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String content = messageField.getText();
        if (content.isEmpty()) return;

        Message msg = new Message(currentUser, chattingWith, content, LocalDateTime.now());

        try {
            messageDao.saveMessage(msg);
            messageField.clear();
            loadMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
