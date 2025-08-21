package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import models.User;
import utility.UIUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDao {
    private final ObservableList<User> userData = FXCollections.observableArrayList();

    public ObservableList<User> caricaUtentiDao(){
        String sql = "SELECT username, tipo_utente, password, medico, informazioni FROM utenti";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
             ResultSet rs = pstmt.executeQuery() ;
            while (rs.next()) {
                userData.add(new User(
                        rs.getString("username"),
                        rs.getString("tipo_utente"),
                        rs.getString("password"),
                        rs.getString("medico"),
                        rs.getString("informazioni")
                ));
            }
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User eliminaUtenteDao(User selectedUser){
        String sql = "DELETE FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selectedUser.getUsername());
            pstmt.executeUpdate();
            userData.remove(selectedUser);
            return selectedUser;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean esisteUtente(User user){
        boolean exists = userData.contains(user);
        if(exists)System.out.println("esiste");
        else System.out.println("non esiste");
        return exists;
    }

    public User aggiungiUtente(User user){
        if(!controlloMedico(user.getMedico())) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :", "Il medico da lei inserito non esiste");
            return null;
        }
        String sql = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getTipoUtente());
            pstmt.setString(3, user.getPassword());
            if(user.getTipoUtente().equals("medico") || user.getTipoUtente().equals("admin")) {
                pstmt.setString(4, "NULL");
            }
            else {
                pstmt.setString(4, user.getMedico());
            }
            pstmt.setString(5, "informazioni...");
            pstmt.executeUpdate();
            userData.add(user);

            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiunto", "Nuovo utente inserito con successo!");
            return user;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void aggiornaUtente(String username, String tipoUtente, String password, String medico, String info, String oldUsername){

        if(tipoUtente.equals("admin") || tipoUtente.equals("medico")){
            medico = "NULL";
        }else {
            if(!controlloMedico(medico)) {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :", "Il medico da lei inserito non esiste");
                return;
            }
        }

        User user = new User(username, tipoUtente, password, medico, info);
        System.out.println("Tipo utente in aggiornaUtente Dao : " + tipoUtente);

        if(oldUsername != null){
            User oldUser = new User(oldUsername, tipoUtente, password, medico, info);
            eliminaUtenteDao(oldUser);
            aggiungiUtente(user);
        }
        else {
            userData.add(user);

            String sql = "UPDATE utenti SET  tipo_utente = ?, password = ?, medico = ?, informazioni = ? WHERE username = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tipoUtente);
                pstmt.setString(2, password);
                pstmt.setString(3, medico);
                pstmt.setString(4, info);
                pstmt.setString(5, username);
                pstmt.executeUpdate();

                UIUtils.showAlert(Alert.AlertType.INFORMATION, "Utente aggiornato", "Utente aggiornato con successo!");
            } catch (Exception ex) {
                ex.printStackTrace();
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Non è stato possibile aggiornare  l'utente");
            }
        }
    }

    // ritorno true se esiste
    private boolean controlloMedico(String medico){
        String sql = "SELECT username FROM utenti WHERE username = ? AND tipo_utente = ?" ;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, medico);
            pstmt.setString(2, "medico");
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
}
