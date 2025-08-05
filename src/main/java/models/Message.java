package models;

import java.time.LocalDateTime;

public class Message {
    private String sender; // "dottore" o "paziente"
    private String receiver;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sender, String receiver, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Getter e Setter
}

