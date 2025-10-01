import controllers.AdminPageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminPageControllerLogicTest {

    private AdminPageController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminPageController();
    }

    @Test
    void testControlloUserValido() throws Exception {
        // username valido (solo lettere e numeri)
        String result = invokeControlloUser("Mario123");
        assertEquals("Mario123", result);
    }

    @Test
    void testControlloUserNonValido() throws Exception {
        // username non valido (contiene simboli)
        String result = invokeControlloUser("??!!");
        assertEquals("", result);
    }

    @Test
    void testControlloPassValida() throws Exception {
        // password valida (rispetta i requisiti: 8+ caratteri, maiuscola, numero, simbolo)
        String result = invokeControlloPass("Password1!");
        assertEquals("Password1!", result);
    }

    @Test
    void testControlloPassNonValida() throws Exception {
        // password non valida (troppo corta e senza simboli)
        String result = invokeControlloPass("abc");
        assertEquals("", result);
    }

    // --- Utility con reflection per accedere ai metodi privati ---

    private String invokeControlloUser(String value) throws Exception {
        var method = AdminPageController.class.getDeclaredMethod("controlloUser", String.class);
        method.setAccessible(true);
        return (String) method.invoke(controller, value);
    }

    private String invokeControlloPass(String value) throws Exception {
        var method = AdminPageController.class.getDeclaredMethod("controlloPass", String.class);
        method.setAccessible(true);
        return (String) method.invoke(controller, value);
    }
}


/*
* Cosa fa questo test
- Verifica che controlloUser accetti stringhe valide e rifiuti quelle invalide.
- Verifica che controlloPass accetti una password robusta e rifiuti una debole.
- Non dipende da JavaFX né dal database: gira in puro JUnit.
*/