package controllers;

import DAO.MessageDao;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.Message;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller della finestra chat.
 * @packege controllers
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/ChatPage.fxml">ChatPage.fxml</a>
 */
public class ChatController {

    @FXML private VBox messageContainer;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;
    /**
     * Oggetto per accesso al database
     * @see DAO.MessageDao
     */
    private final MessageDao messageDao = new MessageDao();
    /**
     * Utente loggato
     */
    private String currentUser;
    /**
     * Utente con cui sto chattando
     */
    private String chattingWith;

    /**
     * Questo metodo ha lo scopo di inizializzare.
     * @param currentUser  utente loggato
     * @param chattingWith utente con cui sto parlando
     */
    public void initializeChat(String currentUser, String chattingWith) {
        this.currentUser = currentUser;
        this.chattingWith = chattingWith;
        loadMessages();
    }

    /**
     * Questo metodo ha lo scopo di auto.aggiornare la chat ogni 10 secondi
     */
    @FXML
    public void initialize() {   // Auto-Aggiornamento della Chat
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> loadMessages()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Questo metodo ha lo scopo di caricare i messaggi.
     * @see DAO.MessageDao
     * @see models.Message
     */
    private void loadMessages() {
        messageContainer.getChildren().clear();
        try {
            List<Message> messages = messageDao.getConversation(currentUser, chattingWith);
            for (Message msg : messages) {
                addMessageToView(msg.getSender(), msg.getContent());
            }
            chatScrollPane.setVvalue(1.0); // Scroll to bottom
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo ha lo scopo di inviare il messaggio.
     * @see DAO.MessageDao
     * @see models.Message
     */
    @FXML
    private void sendMessage() {
        String content = messageField.getText();
        if (content.isEmpty()) return;

        Message msg = new Message(currentUser, chattingWith, content, LocalDateTime.now());

        try {
            messageDao.saveMessage(msg);
            messageField.clear();
            loadMessages();
            // autoscroll in fondo
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo ha lo scopo di aggiungere il messaggio alla chat
     * @param sender  chi sta inviando il messaggio
     * @param content  contenuto messaggio
     */
    private void addMessageToView(String sender, String content) {
        Label messageLabel = new Label(content);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300); // Imposta una larghezza massima ragionevole
        messageLabel.setMinHeight(Label.USE_PREF_SIZE); // Fa sì che cresca in altezza solo per il contenuto
        messageLabel.getStyleClass().add(sender.equals(currentUser) ? "message-user" : "message-other");

        HBox messageBox = new HBox(messageLabel);
        messageBox.setMaxWidth(Double.MAX_VALUE);
        messageBox.setSpacing(5);
        messageBox.getStyleClass().add("message-container");

        if (sender.equals(currentUser)) {
            messageBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        messageContainer.getChildren().add(messageBox);
    }

}
