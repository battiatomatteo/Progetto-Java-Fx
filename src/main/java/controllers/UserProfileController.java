package controllers;

import DAO.AdminDao;
import DAO.UIUtilsDao;
import DAO.UserProfileDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utility.SessionManager;
import utility.UIUtils;
import javafx.scene.control.TextArea;
import view.UserProfileView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Controller della finestra che permette la visione dei dati utente tramite una pagina dedicata.
 * @package controllers
 * @see DAO.UserProfileDao
 * @see DAO.UIUtilsDao
 * @see DAO.AdminDao
 * @see utility.UIUtils
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/UserProfile.fxml">UserProfile.fxml</a>
 */
public class UserProfileController {

    // Attributi della classe
    @FXML private Label nomeLabel, tipoUtenteLabel, infoLabel, telefonoLabel, emailLabel, cognomeLabel, contentRequest, motivoR;
    @FXML private ImageView profileImage;
    @FXML private GridPane infoN, newPass, boxRichieste, boxNewPass;
    @FXML private TextField nomeLabelN, telefonoLabelN, emailLabelN, cognomeLabelN, newPassL, newPassLR;
    @FXML private Button editProf, newPassB, logOutButton, backb, accettaRichiesta, changePass, newImgB;
    @FXML private TextArea commentoArea;
    @FXML private ComboBox<String> tipoRichiesta;

    private UserProfileDao dao = new UserProfileDao();
    private UIUtilsDao daoU = new UIUtilsDao();
    private AdminDao daoA = new AdminDao();
    private String profiloUsername;

    /**
     * Metodo di inizializzazione automatico eseguito al caricamento del controller.
     * @throws Exception
     */
    @FXML
    public void initialize() throws Exception {
        boxRichieste.setVisible(false);
        newPass.setVisible(false);
        infoN.setVisible(false);
        boxNewPass.setVisible(false);

        checkRequet();

        String username = (profiloUsername != null) ? profiloUsername : SessionManager.getCurrentUser();
        infoUser(username);
        caricaImmagine(username);

        backb.setOnAction(e -> {
            try {
                UIUtils.handleBack(username, (Stage) backb.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        if(!daoU.tipoUtente(SessionManager.getCurrentUser()).equals("paziente")){
            editProf.setVisible(false);
            newPassB.setVisible(false);
            newImgB.setVisible(false);
        }

        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));
    }

    /**
     * Questo metodo ha lo scopo di settare tutti i label coi dati del paziente presenti nel database
     * @param username nome utente
     * @see DAO.UIUtilsDao
     */
    public void setProfiloUsername(String username) {
        this.profiloUsername = username;
        infoUser(username);
        caricaImmagine(username);
        if(daoU.tipoUtente(username).equals("admin")){
            checkRequestForAdmin(username);
            accettaRichiesta.setOnAction(e ->  requestAccepted(username));
        }
    }

    /**
     * Questo metodo ha lo scopo di salvare la nuova password nel database
     * @see utility.UIUtils
     * @see DAO.UserProfileDao
     */
    @FXML
    private void cambioPassword(){
        if(newPassLR.getText().equals(newPassL.getText())){
            // salvo la nuova password
            if(!UIUtils.controlloPassword(newPassL.getText())){
                UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :" , "La password da lei inserita non soddisfa i requisiti mini .");
                return;
            }
            System.out.println("Le due password sono uguali .");
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma salvataggio nuova password");
            confirm.setHeaderText(null);
            confirm.setContentText("Sei sicuro di voler cambiare la tua password ? Attenzione non potrai più usare la precedente .");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if(dao.updateNewPass(newPassL.getText(), SessionManager.getCurrentUser())){
                    boxNewPass.setVisible(false);
                    dao.changeStateRequest(SessionManager.getCurrentUser(), "conclusa");
                }
            }
        }else UIUtils.showAlert(Alert.AlertType.ERROR, "Errore :", "Hai inserito due password diverse !");
    }

    /**
     * Questo metodo ha lo scopo di caricare l'immagine profilo utente
     * @param username nome utente
     * @see DAO.UserProfileDao
     */
    private void caricaImmagine(String username) {
        try {
            Image img = dao.caricaImmagineProfilo(username);
            if (img != null && img.getWidth() > 0) {
                profileImage.setImage(img);
            } else {
                profileImage.setImage(new Image("/img/unnamed.jpg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileImage.setImage(new Image("/img/unnamed.jpg"));
        }
    }

    /**
     * Questo metodo ha lo scopo di mostrare le nuove informazioni dell'utente
     */
    @FXML
    private void handleEdit() {
        newPassB.setVisible(false);
        infoN.setVisible(true);
        checkRequet();
    }

    /**
     * Questo metodo ha lo scopo di salvare nel database le nuove informazioni dell'utente
     * @see DAO.UserProfileDao
     */
    @FXML
    private void saveNewInfo(){
        String username = SessionManager.getCurrentUser();
        String newName = nomeLabel.getText();
        String newCognome = cognomeLabel.getText();
        String newEmail = emailLabel.getText();
        String newTelefono = telefonoLabel.getText();

        if(nomeLabelN.getText().isEmpty() || cognomeLabelN.getText().isEmpty() || emailLabelN.getText().isEmpty() || telefonoLabelN.getText().isEmpty()){
            UIUtils.showAlert(Alert.AlertType.ERROR,"Errore", "Non è stato compilato nessun dato, si prega di compilarne almeno uno.");
            infoN.setVisible(false);
            return;
        }

        if(!nomeLabelN.getText().isEmpty()){
            newName = nomeLabelN.getText();
        }
        if(!cognomeLabelN.getText().isEmpty()){
            newCognome = cognomeLabelN.getText();
        }
        if(!emailLabelN.getText().isEmpty()){
            newEmail = emailLabelN.getText();
        }
        if(!telefonoLabelN.getText().isEmpty()){
            newTelefono = telefonoLabelN.getText();
        }

        dao.changeInfo(newName, newCognome, newTelefono, newEmail, username);
        infoUser(username);

        telefonoLabelN.clear();
        emailLabelN.clear();
        cognomeLabelN.clear();
        nomeLabelN.clear();

        infoN.setVisible(false);
    }

    /**
     * Questo metodo ha lo scopo di settare i label coi dati utente presenti nel database
     * @param username nome utente
     * @see DAO.UserProfileDao
     */
    private void infoUser(String username){
        //String username = SessionManager.getCurrentUser();
        ArrayList<String> list = dao.caricoInfoUtente(username);

        if (list != null && list.size() == 6) {
            nomeLabel.setText(list.get(0));
            cognomeLabel.setText(list.get(1));
            tipoUtenteLabel.setText(list.get(2));
            telefonoLabel.setText(list.get(3));
            infoLabel.setText(list.get(4));
            emailLabel.setText(list.get(5));
        } else {
            System.out.println("Dati utente incompleti o null");
        }
    }

    /**
     * Questo metodo ha lo scopo di controllare le richieste
     */
    private void checkRequet() {
        if(dao.hasPendingRequest(SessionManager.getCurrentUser())){
            newPassB.setVisible(false);
        }
        else if(dao.checkNewPass(SessionManager.getCurrentUser())) {
            newPassB.setVisible(false);
            editProf.setVisible(false);
            boxNewPass.setVisible(true);
            System.out.println("Ci sta una richietsa in stato : 'accettata' .");
        }else newPassB.setVisible(true);
    }

    /**
     * Questo metodo ha lo scopo di rendere visibile il box per accettare la richiesta nel caso l'utente loggato sia l'admin
     * @param username nome utente
     */
    private void checkRequestForAdmin(String username){
        // System.out.println("Corrent User : " + SessionManager.getCurrentUser()+ "\nUsername paziente : " + username);
        if(daoU.tipoUtente(SessionManager.getCurrentUser()).equals("admin") && daoA.checkRequest(username).equals("si")){
            // rendo visibile la richiesta all'admin
            dao.setLabelRequest(motivoR, contentRequest, username );
            boxRichieste.setVisible(true);
        }
    }

    /**
     * Questo metodo ha lo scopo di annullare la scelta .
     */
    @FXML
    public void annulla() {
        infoN.setVisible(false);
        checkRequet();
    }

    /**
     * Questo metodo ha lo scopo di salvare la nuova immagine profilo nel database.
     * @see DAO.UserProfileDao
     */
    @FXML
    public void newImg() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona immagine profilo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(profileImage.getScene().getWindow());

        if (file != null) {
            try {
                dao.aggiornaImmagineProfilo(SessionManager.getCurrentUser(), file);
                profileImage.setImage(new Image(new FileInputStream(file)));
            } catch (Exception e) {
                e.printStackTrace();
                UIUtils.showAlert(Alert.AlertType.ERROR,"Errore", "Impossibile salvare l'immagine.");
            }
        }
    }

    /**
     * Questo metodo ha lo scopo di rendere visibili i box per la richiesta della nuova password
     */
    @FXML
    private void newPassword() {
        newPass.setVisible(true);
        editProf.setVisible(false);
    }

    /**
     * Questo metodo ha lo scopo di creare una richiesta per la nuova password
     * @see DAO.UserProfileDao
     */
    public void invioRichiesta() {
        String user = SessionManager.getCurrentUser();
        String content = commentoArea.getText();
        String motivo = tipoRichiesta.getValue();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma invio richiesta");
        confirm.setHeaderText(null);
        confirm.setContentText("Sei sicuro di voler inviare la richiesta ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.sendRequest(user, content, motivo);
        }

        commentoArea.setText("");
        tipoRichiesta.setValue(null);
    }

    /**
     * Questo metodo ha lo scopo di annullare la scelta .
     */
    public void annullaP() {
        newPass.setVisible(false);
        editProf.setVisible(true);
    }

    /**
     * Questo metodo ha lo scopo di cambiare lo stato della richiesta in 'accettata'
     * @param user
     * @see DAO.UserProfileDao
     */
    public void requestAccepted(String user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma richiesta");
        confirm.setHeaderText(null);
        confirm.setContentText("Sei sicuro di voler accettare la richiesta cambio password ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.changeStateRequest(user, "accettata");
            boxRichieste.setVisible(false);
        }
    }
}
