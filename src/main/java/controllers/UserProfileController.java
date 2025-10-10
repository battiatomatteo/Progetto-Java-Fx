package controllers;

import DAO.UserProfileDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import utility.SessionManager;
import utility.UIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class UserProfileController {

    @FXML private Label nomeLabel, tipoUtenteLabel, infoLabel, telefonoLabel, emailLabel, cognomeLabel;
    @FXML private ImageView profileImage;
    @FXML private GridPane infoN;
    @FXML private TextField nomeLabelN, telefonoLabelN, emailLabelN, cognomeLabelN;
    private UserProfileDao dao = new UserProfileDao();

    @FXML
    public void initialize() {

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
    }

    @FXML
    private void handleEdit() {
        infoN.setVisible(true);
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
}
