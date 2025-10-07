package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import models.User;
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
}
