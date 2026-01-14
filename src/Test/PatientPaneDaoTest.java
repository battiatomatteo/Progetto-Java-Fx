import DAO.DBConnection;
import javafx.collections.ObservableList;
import models.FilterDataSetter;
import models.Terapia;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PatientPaneDaoTest {

    private static PatientPaneDaoForTest paneDao;

    @BeforeAll
    static void setup() {
        paneDao = new PatientPaneDaoForTest();
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

            // Inserisco una terapia
            String insertTerapia = """
                INSERT INTO terapie
                (ID_terapia, username, farmaco, count_farmaco, quantità_farmaco, note, stato)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insertTerapia)) {
                ps.setInt(1, 1);
                ps.setString(2, "TestPaziente1");
                ps.setString(3, "Aspirina");
                ps.setString(4, "2");
                ps.setString(5, "500mg");
                ps.setString(6, "Nota");
                ps.setString(7, "TERMINATA");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTerapia)) {
                ps.setInt(1, 2);
                ps.setString(2, "TestPaziente1");
                ps.setString(3, "Aspirina");
                ps.setString(4, "2");
                ps.setString(5, "250mg");
                ps.setString(6, "Nota");
                ps.setString(7, "ATTIVA");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTerapia)) {
                ps.setInt(1, 3);
                ps.setString(2, "TestPaziente1");
                ps.setString(3, "Benagol");
                ps.setString(4, "2");
                ps.setString(5, "250mg");
                ps.setString(6, "Nota");
                ps.setString(7, "SOSPESA");
                ps.executeUpdate();
            }
        }
    }

    // ===================== getTerapieList =====================

    @Test
    void testGetTerapieList() {
        FilterDataSetter filter = new FilterDataSetter("TestPaziente1",FilterDataSetter.ALL_STATUS_VIEWS,FilterDataSetter.ALL_THERAPY);
        ObservableList<Terapia> terapie = paneDao.getTerapieList(filter);

        assertNotNull(terapie);
        assertEquals(3, terapie.size());


        FilterDataSetter filter2 = new FilterDataSetter("TestPaziente1",FilterDataSetter.TERMINATED_STATUS,"Aspirina");
        ObservableList<Terapia> terapie2 = paneDao.getTerapieList(filter2);

        assertNotNull(terapie2);
        assertEquals(1, terapie2.size());
    }

    // ===================== addNewTerapia =====================

    @Test
    void testAddNewTerapia() {
        Terapia t = paneDao.addNewTerapia(
                "TestPaziente1",
                "ATTIVA",
                "Tachipirina",
                "3",
                "1000mg",
                "Nuova terapia"
        );

        assertNotNull(t);
        assertEquals("Tachipirina", t.getFarmaco());
    }

    // ===================== getMaxId =====================

    @Test
    void testGetMaxId() {
        int maxId = paneDao.getMaxId();
        assertEquals(4, maxId);
    }

    // ===================== getInfoUtente =====================

    @Test
    void testGetInfoUtente() {
        String info = paneDao.getInfoUtente("TestPaziente1");

        assertNotNull(info);
        assertEquals("Paziente di Test 1", info);
    }

    // ===================== updateInfoUtente =====================

    @Test
    void testUpdateInfoUtente() {
        paneDao.updateInfoUtente("TestPaziente1", "Info aggiornate");

        String updatedInfo = paneDao.getInfoUtente("TestPaziente1");
        assertEquals("Info aggiornate", updatedInfo);
    }

    // ===================== removeTerapia =====================

    @Test
    void testRemoveTerapia() throws SQLException {
        paneDao.removeTerapia(1);

        try (Connection conn = DBConnection.getConnection()) {
            var rs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM terapie WHERE ID_terapia = 1"
            ).executeQuery();
            rs.next();

            assertEquals(0, rs.getInt(1));
        }
    }

    // ===================== updateTerapia =====================

    @Test
    void testUpdateTerapia() {
        Terapia updated = paneDao.updateTerapia(
                1,
                "SOSPESA",
                "Aulin",
                "1",
                "100mg",
                "Nota aggiornata"
        );

        assertNotNull(updated);
        assertEquals("Aulin", updated.getFarmaco());
        //assertEquals("sospesa", updated.getStato());
    }

    // ===================== cambioVisualizzato =====================

    @Test
    void testCambioVisualizzato() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String insertMsg = """
                INSERT INTO messages(sender, receiver, content, visualizzato)
                VALUES (?, ?, ?, false)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insertMsg)) {
                ps.setString(1, "TestMedico1");
                ps.setString(2, "TestPaziente1");
                ps.setString(3, "Messaggio");
                ps.executeUpdate();
            }
        }

        paneDao.cambioVisualizzato("TestPaziente1", "TestMedico1");

        try (Connection conn = DBConnection.getConnection()) {
            var rs = conn.prepareStatement(
                    "SELECT visualizzato FROM messages"
            ).executeQuery();
            rs.next();

            assertTrue(rs.getBoolean(1));
        }
    }

    // ===================== CLEANUP =====================

    //@AfterEach
    void resetDatabase() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM messages").executeUpdate();
            conn.prepareStatement("DELETE FROM terapie").executeUpdate();
            conn.prepareStatement("DELETE FROM utenti").executeUpdate();
        }
    }
}
