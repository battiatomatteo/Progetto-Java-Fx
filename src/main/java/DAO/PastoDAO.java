package DAO;

import models.Pasto;
import java.util.List;

public interface PastoDAO {
    List<Pasto> getAllPasti();
    Pasto getPastoById(int id); // opzionale se hai un id
    void insertPasto(Pasto pasto);
    void updatePasto(Pasto pasto);
    void deletePasto(Pasto pasto);
}

/* Nel Controller, per esempio:
Connection conn = DriverManager.getConnection(...);
PastoDAO dao = new PastoDAO(conn);
dao.salva(nuovoPasto);  // salva nel DB
*/

// COSE DA AGGIUNGERE :

/*Pasto.java
* package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pasto {
    private final StringProperty pasto;
    private final StringProperty orario;
    private final StringProperty pre;
    private final StringProperty post;

    public Pasto(String pasto, String orario, String pre, String post) {
        this.pasto = new SimpleStringProperty(pasto);
        this.orario = new SimpleStringProperty(orario);
        this.pre = new SimpleStringProperty(pre);
        this.post = new SimpleStringProperty(post);
    }

    public StringProperty pastoProperty() { return pasto; }
    public StringProperty orarioProperty() { return orario; }
    public StringProperty preProperty() { return pre; }
    public StringProperty postProperty() { return post; }

    public String getPasto() { return pasto.get(); }
    public void setPasto(String value) { pasto.set(value); }

    public String getOrario() { return orario.get(); }
    public void setOrario(String value) { orario.set(value); }

    public String getPre() { return pre.get(); }
    public void setPre(String value) { pre.set(value); }

    public String getPost() { return post.get(); }
    public void setPost(String value) { post.set(value); }
}
*/

/* PastoDAOImpl.java
* package dao;

import models.Pasto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PastoDAOImpl implements PastoDAO {
    private final Connection conn;

    public PastoDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Pasto> getAllPasti() {
        List<Pasto> pasti = new ArrayList<>();
        String query = "SELECT pasto, orario, pre, post FROM pasti";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Pasto p = new Pasto(
                    rs.getString("pasto"),
                    rs.getString("orario"),
                    rs.getString("pre"),
                    rs.getString("post")
                );
                pasti.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pasti;
    }

    @Override
    public Pasto getPastoById(int id) {
        // Se non hai una colonna id, puoi non implementarlo o usarne un altro identificatore
        return null;
    }

    @Override
    public void insertPasto(Pasto pasto) {
        String sql = "INSERT INTO pasti (pasto, orario, pre, post) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pasto.getPasto());
            pstmt.setString(2, pasto.getOrario());
            pstmt.setString(3, pasto.getPre());
            pstmt.setString(4, pasto.getPost());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePasto(Pasto pasto) {
        String sql = "UPDATE pasti SET orario = ?, pre = ?, post = ? WHERE pasto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pasto.getOrario());
            pstmt.setString(2, pasto.getPre());
            pstmt.setString(3, pasto.getPost());
            pstmt.setString(4, pasto.getPasto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePasto(Pasto pasto) {
        String sql = "DELETE FROM pasti WHERE pasto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pasto.getPasto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
*/


/* Creazione Connessione DB (esempio SQLite)
* package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
*/

/* Cose da fare nel controllore
*import dao.PastoDAO;
import dao.PastoDAOImpl;
import models.Pasto;
import util.DBConnection;

import java.sql.Connection;
import java.util.List;

public class PatientPageController {
    private PastoDAO pastoDAO;

    @FXML
    private TableView<Pasto> tableView;
    private ObservableList<Pasto> pastiData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            Connection conn = DBConnection.getConnection();
            pastoDAO = new PastoDAOImpl(conn);

            // Carica dati dal DB
            List<Pasto> listaPasti = pastoDAO.getAllPasti();
            pastiData.addAll(listaPasti);
            tableView.setItems(pastiData);

            // configura colonne e cell factory (come già fatto)
            // ...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nuovaSomministrazione() {
        // Esempio: salva tutti i pasti nel DB
        for (Pasto p : pastiData) {
            pastoDAO.updatePasto(p);
        }
    }
}
 */


/*
La tabella pasti nel DB deve essere creata prima (con colonne: pasto TEXT PRIMARY KEY, orario TEXT, pre TEXT, post TEXT ad esempio).
Puoi migliorare con gestione ID, eccezioni, transazioni, ecc.
Puoi aggiungere metodi di ricerca, filtri, ecc.
*/

