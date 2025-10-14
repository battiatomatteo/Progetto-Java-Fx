package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe che gestisce l'accesso al database per i metodi presenti in UIUtils
 * @packege DAO
 */
public class UIUtilsDao {
    // Costanti definite per la classe
    public static int ACCESS_GRANTED = 0;
    public static int ACCESS_ERROR = -1;
    public static int ACCESS_DENIED = -2;

    /**
     * Metodo con lo scopo di verificare l'autenticazione nella pagina di LogIn
     * @param username nome utente
     * @param password password utente
     * @return boolean - true in caso l'autenticazione fosse corretta, false altrimenti.
     */


    public boolean authenticateLogin(String username, String password) {
        String sql = "SELECT password FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword);  // BCrypt.checkpw(...) confronta la password in chiaro con l’hash salvato.
            } else {
                return false; // utente non trovato
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Metodo con lo scopo di verificare l'autenticazione nella pagina del dottore, quando inserisce un nome di un paziente da cercare
     * @param username nome
     * @return int - 0 in caso positivo, -1 se ci sono stati dei problemi, -2 il medico sta cercando un paziente non suo
     */
    public int authenticatePatient(String username,String medico) {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                if(rs.getString("medico").equals(medico))
                    return ACCESS_GRANTED;
                else
                    return ACCESS_DENIED;
            }
            else return ACCESS_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return ACCESS_ERROR;
        }

    }

    /**
     * Metodo con lo scopo di recuperare il tipo utente
     * @param username utente
     * @return string tipoUtente
     */
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

    /**
     * Metodo con lo scopo di ottenere il medico dell'utente passato
     * @param username utente
     * @return stringa - medico dell'utente
     */
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

    /**
     * Metodo con lo scopo di ottenere una lista di tutti i farmaci del paziente presenti nel databse
     * @param username nome paziente
     * @return ArrayList<String> - lista farmaci
     */
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
