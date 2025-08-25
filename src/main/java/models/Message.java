package models;

import java.time.LocalDateTime;

/**
 * Questa classe rappresenta un messaggio scambiato tra due utenti (dottore e paziente).
 * Ogni messaggio contiene informazioni su mittente, destinatario, contenuto e data/ora di invio.
 *
 * @package models
 */
public class Message {

    // Attributi della classe
    /**
     * Chi invia il messaggio
     */
    private String sender; // "dottore" o "paziente"
    /**
     * Chi riceve il messaggio
     */
    private String receiver;
    /**
     * Contenuti del messaggio
     */
    private String content;
    /**
     * Momento in cui si invia il messaggio
     */
    private LocalDateTime timestamp;

    /**
     * Costruttore della classe Message.
     * Crea un messaggio con mittente, destinatario, contenuto e data/ora specificata.
     *
     * @param sender     mittente del messaggio (es. "dottore" o "paziente")
     * @param receiver   destinatario del messaggio
     * @param content    contenuto testuale del messaggio
     * @param timestamp  data e ora di invio del messaggio
     */
    public Message(String sender, String receiver, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Restituisce il mittente del messaggio.
     *
     * @return String - mittente
     */
    public String getSender() {
        return sender;
    }

    /**
     * Restituisce il destinatario del messaggio.
     *
     * @return String - destinatario
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Restituisce il contenuto del messaggio.
     *
     * @return String - contenuto del messaggio
     */
    public String getContent() {
        return content;
    }

    /**
     * Restituisce la data e ora di invio del messaggio.
     *
     * @return LocalDateTime - timestamp del messaggio
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
