package utility;

/**
 * Classe SessionManager
 * @packege utility
 */
public class SessionManager {
    // Attributi della classe
    /**
     * Utente che esegue l'accesso
     */
    private static String currentUser;
    /**
     * Utente che deve ricevere il messaggio
     */
    private static String currentRole; // "doctor" or "patient"

    public static void exit(){
        currentUser = null;
        currentRole = null;
    }

    public static void signIn(String username, String role){
        currentUser = username;
        currentRole = role;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentRole() {
        return currentRole;
    }
}

