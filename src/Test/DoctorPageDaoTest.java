import DAO.DBConnection;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DoctorPageDaoTest {

    private static DoctorPageDaoForTest doctorDao;

    @BeforeAll
    static void setup() {
        doctorDao = new DoctorPageDaoForTest();
    }

    @BeforeEach
    void setupDatabase() throws SQLException {
        resetDatabase();

        try (Connection conn = DBConnection.getConnection()) {

            // Inserisco medico
            String addSampleDoctor1 = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSampleDoctor1)) {
                pstmt.setString(1, "TestMedico1");
                pstmt.setString(2, "medico");
                String password = "TestMedico1.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "NULL");
                pstmt.setString(5, "Medico di Test 1");
                pstmt.executeUpdate();
            }

            // Inserisco medico
            String addSampleDoctor2 = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSampleDoctor2)) {
                pstmt.setString(1, "TestMedico2");
                pstmt.setString(2, "medico");
                String password = "TestMedico2.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "NULL");
                pstmt.setString(5, "Medico di Test 2");
                pstmt.executeUpdate();
            }




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


            String addSamplePatient2 = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSamplePatient2)) {
                pstmt.setString(1, "TestPaziente2");
                pstmt.setString(2, "paziente");
                String password = "TestPaziente2.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "TestMedico1");
                pstmt.setString(5, "Paziente di Test 2");
                pstmt.executeUpdate();
            }


            String addSamplePatient3 = "INSERT INTO utenti(username, tipo_utente, password, medico, informazioni) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(addSamplePatient3)) {
                pstmt.setString(1, "TestPaziente3");
                pstmt.setString(2, "paziente");
                String password = "TestPaziente3.";
                pstmt.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()) );
                pstmt.setString(4, "TestMedico2");
                pstmt.setString(5, "Paziente di Test 3");
                pstmt.executeUpdate();
            }


        }
    }

    @Test
    void testGetAllDoctorPatients() {
        var patients = doctorDao.getAllDoctorPatients("TestMedico1");
        System.out.println(patients);

        assertNotNull(patients);
        assertEquals(2, patients.size());
        assertTrue(patients.contains("TestPaziente1"));
        assertTrue(patients.contains("TestPaziente2"));
        assertFalse(patients.contains("TestPaziente3"));
    }

    @Test
    void testSendAllMess() throws SQLException {
        doctorDao.sendAllMess("TestMedico1", "Messaggio di prova");

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM messages WHERE sender = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "TestMedico1");
                var rs = ps.executeQuery();
                rs.next();

                assertEquals(2, rs.getInt(1), "Devono essere inviati 2 messaggi");
            }
        }
    }

    @Test
    void testRecuperoNotifica() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String insertMessage = """
             INSERT INTO messages(sender, receiver,content, visualizzato)
                VALUES (?, ?, ?, false)
                """;

            String insertMessageVisto = """
             INSERT INTO messages(sender, receiver,content, visualizzato)
                VALUES (?, ?, ?, true)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insertMessage)) {
                ps.setString(1, "TestPaziente1");
                ps.setString(2, "TestMedico1");
                ps.setString(3, "Messaggio da TestPaziente1");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertMessageVisto)) {
                ps.setString(1, "TestPaziente1");
                ps.setString(2, "TestMedico1");
                ps.setString(3, "Messaggio da TestPaziente1 visualizzato");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertMessage)) {
                ps.setString(1, "TestPaziente2");
                ps.setString(2, "TestMedico1");
                ps.setString(3, "Messaggio da TestPaziente2");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertMessage)) {
                ps.setString(1, "TestPaziente3");
                ps.setString(2, "TestMedico2");
                ps.setString(3, "Messaggio da TestPaziente3");
                ps.executeUpdate();
            }
        }

        var notifiche = doctorDao.recuperoNotifica("TestMedico1");
        System.out.println(notifiche);

        assertNotNull(notifiche);
        assertEquals(2, notifiche.size());
        assertTrue(notifiche.get(0).contains("TestPaziente1"));
        assertTrue(notifiche.get(1).contains("TestPaziente2"));
        assertFalse(notifiche.get(0).contains("TestPaziente3"));
    }

    @Test
    void testGetTooManyDaysWithoutLogIn() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {

            String insertRilevazione = """
                INSERT INTO rilevazioni_giornaliere(username, data_rilevazione)
                VALUES (?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(insertRilevazione)) {
                ps.setString(1, "TestPaziente1");
                ps.setString(2, LocalDate.now().minusDays(5).toString());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertRilevazione)) {
                ps.setString(1, "TestPaziente3");
                ps.setString(2, LocalDate.now().minusDays(1).toString());
                ps.executeUpdate();
            }
        }

        var resultDoc1 = doctorDao.getTooManyDaysWithoutLogIn("TestMedico1");
        var resultDoc2 = doctorDao.getTooManyDaysWithoutLogIn("TestMedico2");

        assertNotNull(resultDoc1);
        assertEquals(2, resultDoc1.size(), "Entrambi i pazienti dovrebbero risultare inattivi");


        assertNotNull(resultDoc1);
        assertEquals(0, resultDoc2.size(), "Il paziente e` nel range di tolleranza");



    }

    //@AfterAll
    static void resetDatabase() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM messages").executeUpdate();
            conn.prepareStatement("DELETE FROM rilevazioni_giornaliere").executeUpdate();
            conn.prepareStatement("DELETE FROM utenti").executeUpdate();
        }
    }
}
