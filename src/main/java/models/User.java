package models;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty username;
    private final SimpleStringProperty tipo_utente;
    private final SimpleStringProperty password;
    private final SimpleStringProperty medico;

    public User(String username, String tipo_utente, String password, String medico) {
        this.username = new SimpleStringProperty(username);
        this.tipo_utente = new SimpleStringProperty(tipo_utente);
        this.password = new SimpleStringProperty(password);
        this.medico = new SimpleStringProperty(medico);
    }
    public String getUsername() { return username.get(); }
    public String getTipoUtente() { return tipo_utente.get(); }
    public String getPassword() { return password.get(); }
    public String getMedico() { return medico.get(); }
    public SimpleStringProperty usernameProperty() { return username; }
    public SimpleStringProperty tipo_utenteProperty() { return tipo_utente; }
    public SimpleStringProperty passwordProperty() { return password; }
    public SimpleStringProperty medicoProperty() { return medico; }
}