package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UIUtilsDao extends DBConnection {

    // utilizza il costuttore della classe padre DBConnection
    // che si occupa di effettuare una connessione con il database usato nel progetto
    public UIUtilsDao() {
        super();
    }

    public boolean authenticateLogin(String username, String password) {
        String sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        try ( PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
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
}
