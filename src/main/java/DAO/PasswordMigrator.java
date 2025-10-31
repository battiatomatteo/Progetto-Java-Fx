package DAO;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * Classe che gestisce la migrazione delle password nel caso non siano criptate ma all'interno del database
 * @package DAO
 */
public class PasswordMigrator {

    /**
    * Questo metodo ha lo scopo di trovare le password nel database che non sono state salvate correttamente .
     */
    public static void criptaPasswordEsistenti() {
        String selectSQL = "SELECT username, password FROM utenti";
        String updateSQL = "UPDATE utenti SET password = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String plainPassword = rs.getString("password");

                // Salta se la password è già criptata (opzionale: controlla lunghezza o prefisso)
                if (plainPassword.startsWith("$2a$") || plainPassword.startsWith("$2b$")) {
                    continue;
                }

                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

                updateStmt.setString(1, hashedPassword);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                System.out.println("Password criptata per utente: " + username);
            }

            System.out.println("Migrazione completata.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore durante la migrazione delle password.");
        }
    }
}
