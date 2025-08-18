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

        String username = usernameInput.getText();
        String tipoUtente = tipoUtenteInput.getValue();
        String password = passwordInput.getText();
        String medico = medicoInput.getText();

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

        addButton.setOnAction(e -> addUser(username, tipoUtente, password, medico));
        cancelButton.setOnAction(e -> deleteUser());
        updateButton.setOnAction(e -> updateUser(username, tipoUtente, password, medico));
    }

    private void addUser(String username, String tipoUtente, String password, String medico) {
        if(tipoUtente.equals("admin") || tipoUtente.equals("medico")){
            medico = "NULL";
        }
        User user = new User(username, tipoUtente, password, medico, "informazioni..."); // info paziente vengono create dal medico non dall'admin

        if (username.isEmpty() || tipoUtente.isEmpty() || password.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Compila tutti i campi obbligatori!");
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
        // svuoto i campi
        usernameInput.clear();
        tipoUtenteInput.cancelEdit();
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

    private void updateUser(String username, String tipoUtente, String password, String medico) {
        User selected = table.getSelectionModel().getSelectedItem();
        // User user = new User(username, tipoUtente, password, medico, "informazioni..."); // info paziente vengono create dal medico non dall'admin

        if(username.isEmpty() && tipoUtente.isEmpty() && password.isEmpty()){
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
                    if(username.isEmpty())
                        username = selected.getUsername();
                    if(tipoUtente.isEmpty())
                        tipoUtente = selected.getTipoUtente();
                    if(password.isEmpty())
                        password = selected.getPassword();

                    table.getItems().remove(selected);

                    table.getItems().add(dao.aggiornaUtente(username, tipoUtente, password, medico));
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiornato", " Utente aggiornato con successo!");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}