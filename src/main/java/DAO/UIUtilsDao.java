package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class UIUtilsDao {

    // utilizza il costuttore della classe padre DBConnection
    // che si occupa di effettuare una connessione con il database usato nel progetto
    /*public UIUtilsDao() {
        super();
    }*/

    public boolean authenticateLogin(String username, String password) {
        String sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        try  (Connection conn = DBConnection.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticatePatient(String username) {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String tipoUtente(String username){
        String sql = "SELECT tipo_utente FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo_utente");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDoctorUser(String username){
        String sql = "SELECT medico FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("medico");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getFarmaciPaziente(String username){
        ArrayList<String> farmaciPaziente = new ArrayList<>();
        String sql = "SELECT farmaco FROM terapie WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                farmaciPaziente.add(rs.getString("farmaco"));
            }
            return farmaciPaziente;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
