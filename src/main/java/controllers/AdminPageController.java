package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.User;
import java.sql.*;

public class AdminPageController {

    @FXML private Label label, label2;
    @FXML private Button logoutButton, addButton, cancelButton;
    @FXML private TextField usernameInput, tipoUtenteInput, medicoInput;
    @FXML private PasswordField passwordInput;
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, String> usernameCol, tipoUtenteCol, passwordCol, medicoCol;

    private final ObservableList<User> data = FXCollections.observableArrayList();
    private final String url = "jdbc:sqlite:miodatabase.db";

    @FXML
    private void initialize() {
        usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        tipoUtenteCol.setCellValueFactory(cellData -> cellData.getValue().tipo_utenteProperty());
        passwordCol.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        medicoCol.setCellValueFactory(cellData -> cellData.getValue().medicoProperty());
        table.setItems(data);

        // Carica utenti dal database
        loadUsers();

        logoutButton.setOnAction(e -> {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            try {
                new view.LogInView().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addButton.setOnAction(e -> addUser());
        cancelButton.setOnAction(e -> deleteUser());
    }

    private void loadUsers() {
        data.clear();
        String sql = "SELECT username, tipo_utente, password, medico FROM utenti";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                data.add(new User(
                        rs.getString("username"),
                        rs.getString("tipo_utente"),
                        rs.getString("password"),
                        rs.getString("medico")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addUser() {
        String username = usernameInput.getText();
        String tipoUtente = tipoUtenteInput.getText();
        String password = passwordInput.getText();
        String medico = medicoInput.getText();

        if (username.isEmpty() || tipoUtente.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Errore", "Compila tutti i campi obbligatori!");
            return;
        }

        // Controlla se l'utente esiste già
        String checkSql = "SELECT COUNT(*) FROM utenti WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Errore", "L'utente con questo username esiste già!");
                usernameInput.clear();
                tipoUtenteInput.clear();
                passwordInput.clear();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Inserisci nel database
        String insertSql = "INSERT INTO utenti(username, tipo_utente, password, medico) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, tipoUtente);
            pstmt.setString(3, password);
            pstmt.setString(4, medico);
            pstmt.executeUpdate();

            data.add(new User(username, tipoUtente, password, medico));
            usernameInput.clear();
            tipoUtenteInput.clear();
            passwordInput.clear();
            medicoInput.clear();
            showAlert(Alert.AlertType.INFORMATION, "Utente aggiunto", "Nuovo utente inserito con successo!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteUser() {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma eliminazione");
            confirm.setHeaderText(null);
            confirm.setContentText("Sei sicuro di voler eliminare l'utente " + selectedUser.getUsername() + "?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                String deleteSql = "DELETE FROM utenti WHERE username = ?";
                try (Connection conn = DriverManager.getConnection(url);
                     PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setString(1, selectedUser.getUsername());
                    pstmt.executeUpdate();
                    data.remove(selectedUser);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Nessuna selezione", "Seleziona un utente dalla tabella da eliminare.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}