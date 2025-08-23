package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import models.FilterDataSetter;
import models.Terapia;
import utility.UIUtils;
import java.sql.*;

/**
 * Classe che gestisce l'accesso al database per i metodi presenti in PatientPane
 * @package DAO
 */
public class PatientPaneDao{
    private ObservableList<Terapia> TerapieData = FXCollections.observableArrayList();

    /**
     * Metodo con lo scopo di ottenere la lista delle terapie filtrate
     * @param filter filtro da applicare alle Terapie
     * @return ObservableList<Terapia> - lista di Terapie
     * @see models.Terapia
     * @see models.FilterDataSetter
     */
    public ObservableList<Terapia> getTerapieList(FilterDataSetter filter) {
        TerapieData.clear();
        String sql = "SELECT ID_terapia, stato, farmaco, count_farmaco, quantità_farmaco, note FROM terapie WHERE username = ?" + filter.getSqlView();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, filter.getPatientUserName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TerapieData.add(new Terapia(
                        rs.getInt("ID_terapia"),
                        rs.getString("stato"),
                        rs.getString("farmaco"),
                        rs.getString("count_farmaco"),
                        rs.getString("quantità_farmaco"),
                        rs.getString("note")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return TerapieData;
    }

    /**
     * Metodo con lo scopo di inserire nel database un nuova Terapia al paziente
     * @param username nome paziente
     * @param stato stato Terapia
     * @param farmaco farmaco
     * @param assunzioni numero du assunzioni
     * @param quantita quantità Terapia
     * @param note note sulla Terapia
     * @return Terapia - nuova Terapia da inserire nel databse
     * @see models.Terapia
     */
    public Terapia addNewTerapia(String username, String stato, String farmaco, String assunzioni, String quantita, String note){
        int maxId = getMaxId();
        Terapia t = new Terapia(maxId, stato, farmaco, assunzioni, quantita, note);

        String sql = "INSERT INTO terapie (ID_terapia, username, farmaco, count_farmaco, quantità_farmaco, note, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,maxId);
            pstmt.setString(2, username);
            pstmt.setString(3, farmaco);
            pstmt.setString(4, assunzioni);
            pstmt.setString(5, quantita);
            pstmt.setString(6, note);
            pstmt.setString(7, stato);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return t;
    }

    /**
     * Metodo con lo scopo di recuperar l'id max
     * @return int - id massimo
     */
    public int getMaxId(){
        int nextId = 1;
        String sql = "SELECT MAX(ID_terapia) FROM terapie";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
             ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nextId;
    }

    /**
     * Metodo con lo scopo di ottenere le informazioni del paziente salvati nel database
     * @param username nome paziente
     * @return stringa - informazioni paziente
     */
    public String getInfoUtente(String username) {
        String sql = "SELECT informazioni FROM utenti WHERE username = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("informazioni");
            }

        } catch (SQLException e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore caricamento dati", "Si è verificato un errore nel caricamento dei dati del paziente");
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * Metodo con lo scopo di aggiornare le informazioni del paziente presenti nel database
     * @param username nome paziente
     * @param nuoveNote nove info da salvare
     */
    public void updateInfoUtente(String username, String nuoveNote) {

        String sql = "UPDATE utenti SET informazioni = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, nuoveNote);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

            UIUtils.showAlert(Alert.AlertType.INFORMATION,"Successo", "Le informazioni utente sono state aggiornate.");
        } catch (SQLException ex) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore", "Errore durante il salvataggio delle modifiche.");
            ex.printStackTrace();
        }
    }

    /**
     * Metodo con lo scopo di eliminare una Terapia, sia da database che nella tabella mostrata al medico
     * @param idTerapia id della Terpia da eliminare
     */
    public void removeTerapia(int idTerapia){
        String sql = "DELETE FROM terapie WHERE id_terapia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTerapia);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Metodo con lo scopo di aggiornare una Terapia selezionata dal medico dalla tabella
     * @param idTerapia id della Terapia da aggiornare
     * @param stato stato della Terapia da aggiornare
     * @param farmaco farmaco della Terapia da aggiornare
     * @param assunzioni assunzioni della Terapia da aggiornare
     * @param quantita quantità della Terapia da aggiornare
     * @param note note della Terapia da aggiornare
     * @return Terapia - terapia aggiornata da inserire nella tabella
     * @see models.Terapia
     */
    public Terapia updateTerapia(int idTerapia, String stato, String farmaco, String assunzioni, String quantita, String note){
        Terapia t = new Terapia(idTerapia, stato, farmaco, assunzioni, quantita, note);

        String sql = "UPDATE terapie SET farmaco = ?, count_farmaco = ?, quantità_farmaco = ?, note = ?, stato= ?  WHERE id_terapia = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString( 1, farmaco);
            pstmt.setString(2, assunzioni);
            pstmt.setString(3, quantita);
            pstmt.setString(4, note);
            pstmt.setString(5, stato);
            pstmt.setInt(6, idTerapia);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return t;
    }

    /**
     * Metodo con lo scopo di aggiornare lo stato della visuallizzazione del messaggio
     * @param doctor medico
     * @param patient paziente
     */
    public void cambioVisualizzato(String doctor, String patient){
        String sql = "UPDATE messages SET visualizzato = true WHERE sender = ? AND receiver = ? AND visualizzato = false ";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient);
            pstmt.setString(2, doctor);
            pstmt.executeUpdate();
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Errore sql visual." , "Errore nel cambio o recupero stato visualizzato messaggi .");
        }
    }

}
