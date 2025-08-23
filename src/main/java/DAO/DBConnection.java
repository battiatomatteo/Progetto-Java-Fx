package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che crea una connesione con il database
 * @package DAO
 */
public class DBConnection {
    // Attributi della classe
    private static final String URL = "jdbc:sqlite:miodatabase.db?busy_timeout=5000";

    /**
     * Crea una connesione al database
     * @return La connessione effettuata
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
