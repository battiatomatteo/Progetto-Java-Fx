package models;

import javafx.beans.property.SimpleStringProperty;

/**
 * Questa classe rappresenta un utente del sistema (admin, medico o paziente).
 * Ogni utente ha uno username, un tipo (ruolo), una password, un medico assegnato (se è un paziente)
 * e un campo contenente informazioni sanitarie del paziente.
 *
 * Utilizza JavaFX {@code SimpleStringProperty} per supportare il binding nei componenti grafici.
 *
 * @package models
 */
public class User {

    // Attributi della classe
    /**
     * Username utente
     */
    private final SimpleStringProperty username;
    /**
     * Tipo dell'utente
     */
    private final SimpleStringProperty tipo_utente;
    /**
     * Password dell'utente
     */
    private final SimpleStringProperty password;
    /**
     * Medico dell'utente selezionato
     */
    private final SimpleStringProperty medico;
    /**
     * Informazioni dell'utente
     */
    private final SimpleStringProperty infoPaziente;
    /**
     * Richieste paziente
     */
    private final SimpleStringProperty richieste;

    /**
     * Costruttore della classe User.
     *
     * @param username       nome utente
     * @param tipo_utente    tipo di utente (admin, medico, paziente)
     * @param password       password dell'utente
     * @param medico         medico assegnato (se paziente)
     * @param infoPaziente   informazioni sanitarie (solo per i pazienti)
     */
    public User(String username, String tipo_utente, String password, String medico, String infoPaziente, String richieste) {
        this.username = new SimpleStringProperty(username);
        this.tipo_utente = new SimpleStringProperty(tipo_utente);
        this.password = new SimpleStringProperty(password);
        this.medico = new SimpleStringProperty(medico);
        this.infoPaziente = new SimpleStringProperty(infoPaziente);
        this.richieste = new SimpleStringProperty(richieste);
    }

    /**
     * Restituisce lo username dell'utente.
     * @return String - username
     */
    public String getUsername() { return username.get(); }

    /**
     * Restituisce il tipo di utente.
     * @return String - tipo utente
     */
    public String getTipoUtente() { return tipo_utente.get(); }

    /**
     * Restituisce la password dell'utente.
     * @return String - password
     */
    public String getPassword() { return password.get(); }

    /**
     * Restituisce il medico associato (solo per i pazienti).
     * @return String - nome del medico
     */
    public String getMedico() { return medico.get(); }

    /**
     * Restituisce le informazioni sanitarie del paziente.
     * @return String - info paziente
     */
    public String getInfoPaziente() { return infoPaziente.get(); }

    /**
     *
     * @return
     */
    public String getRichieste() { return richieste.get(); }

    /**
     * Property per lo username (JavaFX binding).
     * @return SimpleStringProperty - username property
     */
    public SimpleStringProperty usernameProperty() { return username; }

    public SimpleStringProperty richiesteProperty() { return richieste; }

    /**
     * Property per il tipo utente (JavaFX binding).
     * @return SimpleStringProperty - tipo utente property
     */
    public SimpleStringProperty tipo_utenteProperty() { return tipo_utente; }

    /**
     * Property per la password (JavaFX binding).
     * @return SimpleStringProperty - password property
     */
    public SimpleStringProperty passwordProperty() { return password; }

    /**
     * Property per il medico associato (JavaFX binding).
     * @return SimpleStringProperty - medico property
     */
    public SimpleStringProperty medicoProperty() { return medico; }

    /**
     * Property per le info paziente (JavaFX binding).
     * @return SimpleStringProperty - info paziente property
     */
    public SimpleStringProperty infoPazienteProperty() { return infoPaziente; }

    /*
     * Controlla se l'oggetto fornito è un'istanza di User.
     * Se sì, verifica se gli username coincidono.
     * In caso contrario, restituisce false.
     */
    @Override
    public boolean equals(Object o) {
        if( o instanceof User user2){
            return username.get().equals(user2.getUsername());
        }
        else return false;
    }
}
