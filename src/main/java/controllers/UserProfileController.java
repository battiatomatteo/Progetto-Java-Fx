package controllers;

import DAO.UIUtilsDao;
import DAO.UserProfileDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utility.SessionManager;
import utility.UIUtils;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import javafx.scene.control.Button;

public class UserProfileController {

    @FXML private Label nomeLabel, tipoUtenteLabel, infoLabel, telefonoLabel, emailLabel, cognomeLabel;
    @FXML private ImageView profileImage;
    @FXML private GridPane infoN, newPass;
    @FXML private TextField nomeLabelN, telefonoLabelN, emailLabelN, cognomeLabelN;
    @FXML private Button editProf, newPassB, logOutButton, backb;
    private UserProfileDao dao = new UserProfileDao();
    private UIUtils daoU = new UIUtils();

    @FXML
    public void initialize() throws Exception {
        newPass.setVisible(false);
        infoN.setVisible(false);
        infoUser();

        try {
            Image img = dao.caricaImmagineProfilo(SessionManager.getCurrentUser());
            if (img != null && img.getWidth() > 0) {
                profileImage.setImage(img);
            } else {
                profileImage.setImage(new Image("/img/unnamed.jpg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileImage.setImage(new Image("/img/unnamed.jpg"));
        }

        String nome = SessionManager.getCurrentUser();
        backb.setOnAction(e -> {
            try {
                daoU.handleBack(nome, (Stage) backb.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Come funziona: Quando clicco sul bottone, prendi la finestra corrente e passala a UIUtils.LogOutButton() per eseguire il logout
        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));
    }

    @FXML
    private void handleEdit() {
        infoN.setVisible(true);
        newPassB.setVisible(false);
    }

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
        infoUser();

        telefonoLabelN.clear();
        emailLabelN.clear();
        cognomeLabelN.clear();
        nomeLabelN.clear();

        infoN.setVisible(false);
    }

    private void infoUser(){
        String username = SessionManager.getCurrentUser();
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

    @FXML
    public void annulla() {
        infoN.setVisible(false);
        newPassB.setVisible(true);
    }

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

    /**public void handleBack() throws Exception {
        Stage stage = (Stage) backb.getScene().getWindow();

        String nome = SessionManager.getCurrentUser();
        String tipo_utente = daoU.tipoUtente(nome);

        SessionManager.signIn(nome,tipo_utente);

        if (tipo_utente.equals("paziente")) {
            new PatientPageView().start(stage);
        } else if (tipo_utente.equals("medico")) {
            new DoctorPageView().start(stage);
        } else {
            new AdminPageView().start(stage);
        }
    }**/

    @FXML
    private void newPassword() {
        newPass.setVisible(true);
        editProf.setVisible(false);
    }


    public void invioRichiesta() {
    }

    public void annullaP() {
        newPass.setVisible(false);
        editProf.setVisible(true);
    }
}
