package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDao extends DBConnection {
    private final ObservableList<User> userData = FXCollections.observableArrayList();
    // utilizza il costuttore della classe padre DBConnection
    // che si occupa di effettuare una connessione con il database usato nel progetto
    public AdminDao() {
        super();
    }

    public ObservableList<User> caricaUtentiDao(){
        String sql = "SELECT username, tipo_utente, password, medico FROM utenti";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                userData.add(new User(
                        rs.getString("username"),
                        rs.getString("tipo_utente"),
                        rs.getString("password"),
                        rs.getString("medico")
                ));
            }
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User eliminaUtenteDao(User selectedUser){
        String deleteSql = "DELETE FROM utenti WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
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
        String insertSql = "INSERT INTO utenti(username, tipo_utente, password, medico) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getTipoUtente());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getMedico());
            pstmt.executeUpdate();
            userData.add(user);
            return user;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
