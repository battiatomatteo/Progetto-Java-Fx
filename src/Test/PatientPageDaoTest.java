import DAO.DBConnection;
import models.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PatientPageDaoTest {

    private static PatientPageDaoForTest patientDao;

    @BeforeAll
    static void setup() {
        patientDao = new PatientPageDaoForTest();
    }

    @BeforeEach
    void setupDatabase() throws SQLException {
        resetDatabase();

        try (Connection conn = DBConnection.getConnection()) {

            String addSamplePatient1 = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSamplePatient1)) {
                pstmt.setString(1, "TestPaziente1");
                pstmt.setString(2, "paziente");
                String password = "TestPaziente1.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "TestMedico1");
                pstmt.setString(5, "Paziente di Test 1");
                pstmt.executeUpdate();
            }
        }
    }

    // ===================== somministrazioneTabella =====================

    @Test
    void testSomministrazioneTabella() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String insert = """
                INSERT INTO rilevazioni_giornaliere
                (data_rilevazione, orario, rilevazione_pre_pasto, rilevazione_post_pasto, username)
                VALUES (?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, LocalDate.now().toString());
                ps.setString(2, "08:00");
                ps.setFloat(3, 90);
                ps.setFloat(4, 120);
                ps.setString(5, "TestPaziente1");
                ps.executeUpdate();
            }
        }

        Map<String, Pasto> result = patientDao.somministrazioneTabella("TestPaziente1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("08:00"));
        assertEquals("Colazione", result.get("08:00").getPasto());
    }

    // ===================== addSomministrazione =====================

    @Test
    void testAddSomministrazione() {
        Day day = new Day();

        day.addPasto(new Pasto("Colazione", "08:00", 100, 130));
        day.addPasto(new Pasto("Pranzo", "13:00", 110, 140));

        boolean result = patientDao.addSomministrazione(day, "TestPaziente1");

        assertTrue(result);
    }

    // ===================== checkSomministrazione =====================

    @Test
    void testCheckSomministrazione() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Connection conn = DBConnection.getConnection()) {
            String insert = """
                INSERT INTO rilevazioni_giornaliere
                (data_rilevazione, orario, username)
                VALUES (?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, LocalDate.now().format(formatter));
                ps.setString(2, "08:00");
                ps.setString(3, "TestPaziente1");
                ps.executeUpdate();
            }
        }

        boolean result = patientDao.checkSomministarazione(
                "08:00",
                LocalDate.now(),
                formatter,
                "TestPaziente1"
        );

        assertFalse(result);
    }

    // ===================== messageSomm =====================

    @Test
    void testMessageSomm() throws SQLException {
        patientDao.messageSomm("Test messaggio", "TestMedico1", "TestPaziente1");

        try (Connection conn = DBConnection.getConnection()) {
            var rs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM messages WHERE content = 'Test messaggio'"
            ).executeQuery();
            rs.next();

            assertEquals(1, rs.getInt(1));
        }
    }

    // ===================== recuperoNotifica =====================

    @Test
    void testRecuperoNotifica() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String insert = """
                INSERT INTO messages(sender, receiver, content, visualizzato)
                VALUES (?, ?, ?, false)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, "TestMedico1");
                ps.setString(2, "TestPaziente1");
                ps.setString(3, "Messaggio");
                ps.executeUpdate();
            }
        }

        boolean result = patientDao.recuperoNotifica("TestPaziente1");

        assertTrue(result);
    }

    // ===================== cambioVisualizzato =====================

    @Test
    void testCambioVisualizzato() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("""
                INSERT INTO messages(sender, receiver, content, visualizzato)
                VALUES ('TestMedico1','TestPaziente1','msg',false)
            """).executeUpdate();
        }

        patientDao.cambioVisualizzato("TestMedico1", "TestPaziente1");

        try (Connection conn = DBConnection.getConnection()) {
            var rs = conn.prepareStatement("""
                SELECT visualizzato FROM messages
            """).executeQuery();
            rs.next();

            assertTrue(rs.getBoolean(1));
        }
    }

    // ===================== cercoSintomi =====================

    @Test
    void testCercoSintomi() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO rilevazioni_giornaliere " +
                    "(username, data_rilevazione, note_rilevazione) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "TestPaziente1");
                ps.setString(2, LocalDate.now().toString());
                ps.setString(3, "note...");
                ps.executeUpdate();
            }

        }

        boolean result = patientDao.cercoSintomi(
                "Nuova nota",
                "TestPaziente1",
                LocalDate.now(),
                formatter
        );

        assertTrue(result);

        boolean result2 = patientDao.cercoSintomi(
                "Nuova nota",
                "TestPaziente1",
                LocalDate.now(),
                formatter
        );

        assertFalse(result2);
    }

    // ===================== CLEANUP =====================

    //@AfterEach
    void resetDatabase() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM messages").executeUpdate();
            conn.prepareStatement("DELETE FROM rilevazioni_giornaliere").executeUpdate();
            conn.prepareStatement("DELETE FROM utenti").executeUpdate();
        }
    }
}
