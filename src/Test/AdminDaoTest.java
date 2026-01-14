import DAO.DBConnection;
import javafx.collections.ObservableList;
import models.User;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class AdminDaoTest {
    private static AdminDaoForTest adminDao;

    @BeforeAll
    static void setup() {
        // Inizializzare la connessione al database di test
        adminDao = new AdminDaoForTest();
    }

    @BeforeEach
    void setupDatabase() throws SQLException {
        // Esegui il reset del database e lo preparo per i test
        reset();
        try (Connection conn = DBConnection.getConnection()) {
            String addSampleDoctor = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSampleDoctor)) {
                pstmt.setString(1, "TestMedico1");
                pstmt.setString(2, "medico");
                String password = "TestMedico1.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "NULL");
                pstmt.setString(5, "Medico di Test");
                pstmt.executeUpdate();
            }
        }
    }

    @Test
    void testAggiungiUtente() {
        User user = new User("testUser", "paziente", "password123", "TestMedico1", "info", "no");
        User addedUser = adminDao.aggiungiUtente(user);

        assertNotNull(addedUser, "L'utente dovrebbe essere aggiunto correttamente.");
        assertEquals("testUser", addedUser.getUsername(), "Il nome utente dovrebbe essere 'testUser'.");
    }
    @Order(1)

    @Test
    void testCaricaUtentiDao() {
        User user1 = new User("testUser1", "paziente", "password123", "medico1", "info", "no");
        adminDao.aggiungiUtente(user1);

        ObservableList<User> utenti = adminDao.caricaUtentiDao();

        assertNotNull(utenti, "La lista degli utenti non dovrebbe essere null.");
        assertTrue(utenti.size() > 0, "La lista degli utenti dovrebbe contenere almeno un utente.");
    }

    @Test
    void testEliminaUtente() {
        User user2 = new User("testUser2", "paziente", "password123", "medico2", "info", "no");
        adminDao.aggiungiUtente(user2);

        User deletedUser = adminDao.eliminaUtenteDao(user2);

        assertNotNull(deletedUser, "L'utente dovrebbe essere eliminato correttamente.");
        assertFalse(adminDao.esisteUtente(user2), "L'utente non dovrebbe più esistere dopo essere stato eliminato.");
    }

    @Test
    void testAggiornaUtente() {
        User user3 = new User("testUser3", "paziente", "password123", "TestMedico1", "info", "no");
        adminDao.aggiungiUtente(user3);

        adminDao.aggiornaUtente("testUser3", "paziente", "newPassword123", "TestMedico1", "info aggiornata", "testUser3");

        ObservableList<User> utenti = adminDao.caricaUtentiDao();
        User updatedUser = utenti.stream().filter(u -> u.getUsername().equals("testUser3")).findFirst().orElse(null);

        assertNotNull(updatedUser, "L'utente aggiornato dovrebbe essere presente.");
        assertEquals("newPassword123", updatedUser.getPassword(), "La password dovrebbe essere aggiornata.");
        assertEquals("TestMedico1", updatedUser.getMedico(), "Il medico dovrebbe essere aggiornato.");
    }

    @Test
    void testEsisteUtente() {
        User user4 = new User("testUser4", "paziente", "password123", "TestMedico1", "info", "no");
        adminDao.aggiungiUtente(user4);

        assertTrue(adminDao.esisteUtente(user4), "L'utente dovrebbe esistere.");

        User userNonEsistente = new User("nonExistentUser", "paziente", "password123", "medico5", "info", "no");
        assertFalse(adminDao.esisteUtente(userNonEsistente), "L'utente non dovrebbe esistere.");
    }

    @Test
    void testControlloMedico() {
        boolean medicoEsistente = adminDao.controlloMedico("TestMedico1");
        System.out.println(medicoEsistente);
        boolean medicoNonEsistente = adminDao.controlloMedico("nonExistentMedico");
        System.out.println(medicoNonEsistente);

        assertTrue(medicoEsistente, "Il medico dovrebbe esistere nel sistema.");
        assertFalse(medicoNonEsistente, "Il medico non dovrebbe esistere nel sistema.");
    }

    @Test
    void testCheckRequest() {
        // Test che verifica se la funzione checkRequest funziona correttamente
        String result = adminDao.checkRequest("testUser");
        assertEquals("no", result, "Non ci dovrebbero essere richieste in corso.");
    }

    @AfterAll
    static void reset()  throws SQLException {
        // Pulisce la connessione del database dopo tutti i test
        try (Connection conn = DBConnection.getConnection()) {
            String deleteQuery = "DELETE FROM utenti";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.executeUpdate();
            }
        }
    }
}
