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

    @FXML private Label label, label2;
    @FXML private Button logoutButton, addButton, cancelButton;
    @FXML private TextField usernameInput, tipoUtenteInput, medicoInput;
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

        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logoutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logoutButton.getScene().getWindow()));

        addButton.setOnAction(e -> addUser());
        cancelButton.setOnAction(e -> deleteUser());
    }

    private void addUser() {
        String username = usernameInput.getText();
        String tipoUtente = tipoUtenteInput.getText();
        String password = passwordInput.getText();
        String medico = medicoInput.getText();
        User user = new User(username, tipoUtente, password, medico, ""); // info paziente vengono create dal medico non dall'admin

        if (username.isEmpty() || tipoUtente.isEmpty() || password.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Compila tutti i campi obbligatori!");
            return;
        }
        // Controlla se l'utente esiste già
        if(dao.esisteUtente(user)){
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "L'utente con questo username esiste già!");
            usernameInput.clear();
            tipoUtenteInput.clear();
            passwordInput.clear();
            return;
        }

        // Inserisci nel database
        dao.aggiungiUtente(user);
        // svuoto i campi
        usernameInput.clear();
        tipoUtenteInput.clear();
        passwordInput.clear();
        medicoInput.clear();
        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiunto", "Nuovo utente inserito con successo!");

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
}