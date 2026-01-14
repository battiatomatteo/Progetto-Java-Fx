
import DAO.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe che gestisce l'accesso al database per la pagina Admin
 * @package DAO
 */
public class AdminDaoForTest {
    // Attributi della classe
    /**
     * Tutte le operazioni che effettuano modifiche al database vengono riflesse anche sulla lista userData
     */
    private final ObservableList<User> userData = FXCollections.observableArrayList();

    /**
     * Questo metodo restituisce una lista con tutti gli utenti presenti nel database
     * @return ObservableList<User> - Lista ordinata di tutti gli utenti
     * @see User
     */
    public ObservableList<User> caricaUtentiDao(){
        String sql = "SELECT username, tipo_utente, password, medico, informazioni FROM utenti";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
             ResultSet rs = pstmt.executeQuery() ;
            while (rs.next()) {
                String username = rs.getString("username");

                userData.add(new User(
                        username,
                        rs.getString("tipo_utente"),
                        rs.getString("password"),
                        rs.getString("medico"),
                        rs.getString("informazioni"),
                        checkRequest(username)  // da controllare
                ));
            }
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Questo metodo ha lo scopo di eliminare un utente dal database
     * @param selectedUser L'utente da eliminare
     * @return User - L'utente che è stato eliminato
     * @see User
     */
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

    /**
     * Questo metodo controlla se un utente esiste
     * @param user Utente da controllare
     * @return boolean - Esito della valutazione
     * @see User
     */
    public boolean esisteUtente(User user){
        boolean exists = userData.contains(user);
        if(exists)System.out.println("esiste");
        else System.out.println("non esiste");
        return exists;
    }

    /**
     * Questo metodo ha lo scopo di aggiungere un utente al database
     * @param user Utente da aggiungere
     * @return User - Utente aggiunto al database
     * @see User
     */
    public User aggiungiUtente(User user){
        if(!controlloMedico(user.getMedico())) {
            System.out.println("Errore : Il medico da lei inserito non esiste");
            return null;
        }

        // 🔐 Cripta la password prima di salvarla
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        String sql = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getTipoUtente());
            pstmt.setString(3, hashedPassword);
            if(user.getTipoUtente().equals("medico") || user.getTipoUtente().equals("admin")) {
                pstmt.setString(4, "NULL");
            }
            else {
                pstmt.setString(4, user.getMedico());
            }
            pstmt.setString(5, "informazioni...");
            pstmt.executeUpdate();
            userData.add(user);

            System.out.println("Utente aggiunto Nuovo utente inserito con successo!");
            return user;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Questo metodo ha lo scopo di modificare un utente già presente nel database
     * @param username nuovo username dell'utente (nel caso in cui venga modificato)
     * @param tipoUtente Tipo dell'utente
     * @param password Password dell'utente
     * @param medico Medico associato all'utente
     * @param info Informazioni relative all'utente
     * @param oldUsername username precedente dell'utente
     */
    public void aggiornaUtente(String username, String tipoUtente, String password, String medico, String info, String oldUsername){

        if(tipoUtente.equals("admin") || tipoUtente.equals("medico")){
            medico = "NULL";
        }else {
            if(!controlloMedico(medico)) {
                System.out.println("Errore : Il medico da lei inserito non esiste");
                return;
            }
        }

        User user = new User(username, tipoUtente, password, medico, info, checkRequest(username));
        System.out.println("Tipo utente in aggiornaUtente Dao : " + tipoUtente);

        if(oldUsername != null){
            User oldUser = new User(oldUsername, tipoUtente, password, medico, info, checkRequest(oldUsername));
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

                System.out.println("Utente aggiornato Utente aggiornato con successo!");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println( "Errore Non è stato possibile aggiornare  l'utente");
            }
        }
    }

    /**
     * Questo metodo valuta se l'utente è un medico
     * @param medico Utente da valutare
     * @return boolean - Esito della valutazione
     */
    boolean controlloMedico(String medico){
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


    /**
     * Questo metodo ha lo scopo di controllare se sono presenti delle richieste 'in corso...'
     * @param user
     * @return
     */
    public String checkRequest(String user) {
        String sql = "SELECT COUNT(*) FROM richieste WHERE usernamePaziente = ? AND stato = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, "in corso...");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "si";
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore nel controllo richieste in corso", e);
        }
        return "no";
    }
}
