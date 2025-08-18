package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import models.FilterDataSetter;
import models.Terapia;
import utility.UIUtils;
import java.sql.*;


public class PatientPaneDao{
    private ObservableList<Terapia> TerapieData = FXCollections.observableArrayList();

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
