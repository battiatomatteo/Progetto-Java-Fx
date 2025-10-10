package DAO;

import javafx.scene.control.Alert;
import utility.UIUtils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

}
