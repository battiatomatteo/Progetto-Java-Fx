package DAO;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.mindrot.jbcrypt.BCrypt;
import utility.UIUtils;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Classe che gestisce l'accesso al database per la pagina profilo utente
 * @package DAO
 */
public class UserProfileDao {

    /**
     * Questo metodo ha lo scopo di caricare tutte le info dell'utente presenti nel db
     * @param user nome utente
     * @return ArrayList<String> - contiene le info utente
     */
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

    /**
     * Questo metodo ha lo scopo di salvare le nuove info dell'utente nel db
     * @param nome nome utente
     * @param cognome cognome utente
     * @param telefono telefono utente
     * @param mail mail utente
     * @param user username utente
     */
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

    /**
     * Questo metodo ha lo scopo di aggiornare l'immagine profilo utente nel db
     * @param user paziente
     * @param fileImg immagine profilo da salvare
     */
    public void aggiornaImmagineProfilo(String user, File fileImg){
        String sql = "UPDATE utenti SET img_profilo = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            FileInputStream fis = new FileInputStream(fileImg);
            stmt.setBinaryStream(1, fis, (int) fileImg.length());
            stmt.setString(2, user);
            stmt.executeUpdate();
        }catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore." ,"Errore durante il salvataggio dell'immagine .");
        }
    }

    /**
     * Questo metodo ha lo scopo di caricare l'immagine profilo utente prensete nel db
     * @param username utente
     * @return Image - immagine profilo
     */
    public Image caricaImmagineProfilo(String username) {
        String sql = "SELECT img_profilo FROM utenti WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                InputStream is = rs.getBinaryStream("img_profilo");
                if (is != null) {
                    return new Image(is);
                }
            }
        }catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore." ,"Errore durante il caricamento dell'immagine .");
        }
        return null;
    }

    /**
     * Questo metodo ha lo scopo di creare una nuova richiesta nel db per l'utente loggato
     * @param user username
     * @param content contenuto richiesta
     * @param motivo motivo richiesta
     */
    public void sendRequest(String user, String content, String motivo) {
        String sql = "INSERT INTO richieste (usernamePaziente, contenutoRichiesta, motivo, stato ) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user);
            stmt.setString(2, content);
            stmt.setString(3, motivo);
            stmt.setString(4, "in corso...");
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Questo metodo ha lo scopo di controllare se ci sono richieste in corso
     * @param user username
     * @return boolean - true se ci sono richieste in corso, false altrimenti
     */
    public boolean hasPendingRequest(String user) {
        String sql = "SELECT COUNT(*) FROM richieste WHERE usernamePaziente = ? AND stato = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user);
            stmt.setString(2, "in corso...");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // true se ci sono richieste in corso
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore nel controllo richieste in corso", e);
        }
        return false; // Nessuna richiesta trovata o errore
    }

    /**
     * Questo metodo ha lo scopo di settare i label delle richieste
     * @param content contenuto richiesta
     * @param motivo motivo richiesta
     * @param user username
     */
    public void setLabelRequest(Label content, Label motivo, String user) {
        String sql = "SELECT contenutoRichiesta, motivo FROM richieste WHERE usernamePaziente = ? AND stato = ? ORDER BY idRichiesta DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, "in corso...");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    content.setText(rs.getString("contenutoRichiesta"));
                    motivo.setText(rs.getString("motivo"));
                }
            }
        } catch (Exception e) {
            content.setText("Errore nel caricamento");
            motivo.setText("Controlla la connessione");
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :" , "Si è verificato un problema nel caricamento dei dati .");
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo ha lo scopo di cambiare lo stato della richiesta
     * @param user username
     * @param newState nuovo stato richiesta
     */
    public void changeStateRequest(String user, String newState) {
        // String sql = "UPDATE richieste SET stato = ? WHERE usernamePaziente = ? AND stato = ?";
        String sql = "UPDATE richieste SET stato = ? WHERE usernamePaziente = ? ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // cambio lo stato della richiesta da 'in corso...' -> 'accettata' -> conclusa'
            stmt.setString(1, newState);
            stmt.setString(2, user);
            //stmt.setString(3, "in corso...");
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                UIUtils.showAlert(Alert.AlertType.INFORMATION, "Cambio stato richiesta", "Stato aggiornato a " + newState + " per " + user );
                System.out.println("Stato aggiornato a " + newState + " per " + user);
            } else if(newState.equals("in corso...")) {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Nessuna richiesta 'in corso...' trovata per " + user);
                System.out.println("Nessuna richiesta 'in corso...' trovata per " + user);
            } else{
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Nessuna richiesta '" + newState + "' trovata per " + user);
                System.out.println("Nessuna richiesta '" + newState + "' trovata per " + user);
            }
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante l'aggiornamento dello stato richiesta");
            throw new RuntimeException("Errore durante l'aggiornamento dello stato richiesta", e);
        }
    }

    /**
     * Questo metodo ha lo scopo di fare un controllo della nuova password
     * @param currentUser username
     * @return boolean - true o false
     */
    public boolean checkNewPass(String currentUser) {
        String sql = "SELECT COUNT(*) FROM richieste WHERE usernamePaziente = ? AND stato = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentUser);
            stmt.setString(2, "accettata");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore nel controllo richiesta accettata", e);
        }
        return false;
    }

    /**
     * Questo metodo ha lo scopo di aggiornare la password nel db
     * @param newPass nuova password
     * @param user username
     * @return boolean - true o false
     */
    public boolean updateNewPass(String newPass, String user) {
        // 🔐 Cripta la password prima di salvarla
        String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
        String sql = "UPDATE utenti SET password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, hashedPassword);
            stmt.setString(2, user);
            stmt.executeUpdate();
            return true;
        }catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore." ,"Errore sql durante il salvataggio della nuova password .");
            return false;
        }
    }
}
