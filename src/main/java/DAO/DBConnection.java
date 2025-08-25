package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che crea una connessione con il database
 * @package DAO
 */
public class DBConnection {
    // Costanti della classe
    /**
     * URL al database con tempo di attesa di 3 secondi.
     */
    private static final String URL = "jdbc:sqlite:miodatabase.db?busy_timeout=3000";

    /**
     * Crea una connessione al database
     * @return La connessione effettuata
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
