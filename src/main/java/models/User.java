package models;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty username;
    private final SimpleStringProperty tipo_utente;
    private final SimpleStringProperty password;
    private final SimpleStringProperty medico;
    private final SimpleStringProperty infoPaziente;

    public User(String username, String tipo_utente, String password, String medico, String infoPaziente) {
        this.username = new SimpleStringProperty(username);
        this.tipo_utente = new SimpleStringProperty(tipo_utente);
        this.password = new SimpleStringProperty(password);
        this.medico = new SimpleStringProperty(medico);
        this.infoPaziente = new SimpleStringProperty(infoPaziente);
    }
    public String getUsername() { return username.get(); }
    public String getTipoUtente() { return tipo_utente.get(); }
    public String getPassword() { return password.get(); }
    public String getMedico() { return medico.get(); }
    public String getInfoPaziente() { return infoPaziente.get(); }
    public SimpleStringProperty usernameProperty() { return username; }
    public SimpleStringProperty tipo_utenteProperty() { return tipo_utente; }
    public SimpleStringProperty passwordProperty() { return password; }
    public SimpleStringProperty medicoProperty() { return medico; }
    public SimpleStringProperty infoPazienteProperty() { return infoPaziente; }

    /*
    * controlla se o è un istanza di utente all'ora entro nell'if
    * altrimenti return false
    * all'interno dell'if controllo se hanno lo stesso username
    * */
    @Override
    public boolean equals(Object o) {
        if( o instanceof User user2){
            return username.get().equals(user2.getUsername());
        }
        else return false;
    }
}

