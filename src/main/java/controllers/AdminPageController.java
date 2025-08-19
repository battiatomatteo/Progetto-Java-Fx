package controllers;

import DAO.AdminDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.User;
import utility.UIUtils;
import java.sql.*;

public class AdminPageController {

    @FXML private Button logoutButton, addButton, cancelButton, updateButton;
    @FXML private TextField usernameInput, medicoInput;
    @FXML private ComboBox<String> tipoUtenteInput;
    @FXML private PasswordField passwordInput;
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, String> usernameCol, tipoUtenteCol, passwordCol, medicoCol, infoCol;
    private AdminDao dao = new AdminDao();

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        tipoUtenteCol.setCellValueFactory(cellData -> cellData.getValue().tipo_utenteProperty());
        passwordCol.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        medicoCol.setCellValueFactory(cellData -> cellData.getValue().medicoProperty());
        infoCol.setCellValueFactory(cellData -> cellData.getValue().infoPazienteProperty());
        table.setItems(dao.caricaUtentiDao());         // Carica utenti dal database

        tipoUtenteInput.getItems().addAll("medico", "paziente", "admin");



        //Ogni volta che cambia la selezione della comboBox, il listener controlla il nuovo valore (newVal)
        tipoUtenteInput.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("medico".equalsIgnoreCase(newVal) || "admin".equalsIgnoreCase(newVal)) {
                medicoInput.setDisable(true);   // blocca il campo
                medicoInput.clear();
            } else {
                medicoInput.setDisable(false);  // riattiva il campo
            }
        });

        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logoutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logoutButton.getScene().getWindow()));

        addButton.setOnAction(e -> addUser());
        cancelButton.setOnAction(e -> deleteUser());
        updateButton.setOnAction(e -> updateUser());
    }

    private void addUser() {
        String username = controlloUser(usernameInput.getText());
        String tipoUtente = tipoUtenteInput.getValue();
        String password = controlloPass(passwordInput.getText());
        String medico = medicoInput.getText();

        if(tipoUtente.equals("admin") || tipoUtente.equals("medico")){
            medico = "NULL";
        }
        User user = new User(username, tipoUtente, password, medico, "informazioni..."); // info paziente vengono create dal medico non dall'admin

        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Compila correttamente tutti i campi obbligatori!");
            return;
        }
        // Controlla se l'utente esiste già
        if(dao.esisteUtente(user)){
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "L'utente con questo username esiste già!");
            usernameInput.clear();
            tipoUtenteInput.cancelEdit();
            passwordInput.clear();
            return;
        }

        // Inserisci nel database
        dao.aggiungiUtente(user);
        //table.getItems().add(user);
        // svuoto i campi
        usernameInput.clear();
        tipoUtenteInput.cancelEdit();
        passwordInput.clear();
        medicoInput.clear();
        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiunto", "Nuovo utente inserito con successo!");

    }

    private String controlloUser(String username){
        if (!UIUtils.controlloParolaStringa(username)) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore !", "Il valore username non è valido , riprova .");
            usernameInput.clear();
            return "";
        }
        return username;
    }

    private String controlloPass(String password){
        if (!UIUtils.controlloPassword(password)) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore !", "Il valore della password non è valido ( deve avere almeno " +
                    " 8 caratteri di cui una maiuscola , un numero ed un simbolo , riprova .");
            passwordInput.clear();
            return "";
        }
        return password;
    }

    private void deleteUser() {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma eliminazione");
            confirm.setHeaderText(null);
            confirm.setContentText("Sei sicuro di voler eliminare l'utente " + selectedUser.getUsername() + "?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                dao.eliminaUtenteDao(selectedUser);
            }
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Seleziona un utente dalla tabella da eliminare.");
        }
    }

    private void updateUser() {

        String username = usernameInput.getText();
        String tipoUtente = tipoUtenteInput.getValue();
        String password = passwordInput.getText();
        String medico = medicoInput.getText();

        String oldUsername = null;

        User selected = table.getSelectionModel().getSelectedItem();
        // User user = new User(username, tipoUtente, password, medico, "informazioni..."); // info paziente vengono create dal medico non dall'admin

        if(username.isEmpty() && tipoUtente == null && password.isEmpty() ){
            UIUtils.showAlert(Alert.AlertType.WARNING, "Campi mancanti", "Compila almeno un campo per modificare");
            return;
        }

        try{
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Conferma modifiche");
                confirm.setHeaderText(null);
                confirm.setContentText("Vuoi davvero modificare questo utente?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    if(username.isEmpty()) {
                        username = selected.getUsername();
                    }
                    else{
                        oldUsername = selected.getUsername();

                    }
                    if(tipoUtente == null)
                        tipoUtente = selected.getTipoUtente();
                    if(password.isEmpty())
                        password = selected.getPassword();

                    table.getItems().remove(selected);

                    dao.aggiornaUtente(username, tipoUtente, password, medico, selected.getInfoPaziente() , oldUsername);
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiornato", " Utente aggiornato con successo!");
                    // svuoto i campi
                    usernameInput.clear();
                    tipoUtenteInput.cancelEdit();
                    passwordInput.clear();
                    medicoInput.clear();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}