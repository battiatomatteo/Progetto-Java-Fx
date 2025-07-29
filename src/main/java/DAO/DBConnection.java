package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    private static final String URL = "jdbc:sqlite:miodatabase.db";
    protected Connection conn;

    /*
    * crea una connessione con il database
    * */
    public DBConnection(){
        try {
            this.conn = DBConnection.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}