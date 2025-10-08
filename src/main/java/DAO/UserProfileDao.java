package DAO;

import javafx.scene.control.Alert;
import utility.UIUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserProfileDao {


    public ArrayList<String> caricoInfoUtente(String user){
        ArrayList<String> list = new ArrayList<>();
        String sql = "SELECT nome, cognome, tipo_utente, telefono, informazioni, mail FROM utenti WHERE username = ? ";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery() ;
            while (rs.next()) {
                list.add(rs.getString("nome"));
                list.add(rs.getString("cognome"));
                list.add(rs.getString("tipo_utente"));
                list.add(rs.getString("telefono"));
                list.add(rs.getString("informazioni"));
                list.add(rs.getString("mail"));
            }
            return list;
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore sql visual." , "Errore nel recupero dei dati .");
            return null;
        }
    }

    public void changeInfo(String nome, String cognome, String telefono, String mail, String user) {
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, telefono = ?, mail = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, telefono);
            pstmt.setString(4, mail);
            pstmt.setString(5, user);
            pstmt.executeUpdate();
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Dati salvati", "I dati nuovi sono stati salvati con successo.");
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore." ,"Errore durante il salvataggio dei nuovi dati ");
        }
    }
}
