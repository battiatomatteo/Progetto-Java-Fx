package controllers;

import DAO.UserProfileDao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import utility.SessionManager;

import java.util.ArrayList;

public class UserProfileController {

    @FXML private Label nomeLabel, tipoUtenteLabel, infoLabel, telefonoLabel, emailLabel, cognomeLabel;
    @FXML private ImageView profileImage;
    @FXML private GridPane infoN;
    @FXML private TextField nomeLabelN, telefonoLabelN, emailLabelN, cognomeLabelN;
    private UserProfileDao dao = new UserProfileDao();

    @FXML
    public void initialize() {

        // Immagine profilo
        Image image = new Image("/img/unnamed.jpg");
        profileImage.setImage(image);

        infoN.setVisible(false);

        infoUser();
    }

    @FXML
    private void handleEdit() {
        // TODO: apri form modifica profilo
        infoN.setVisible(true);

    }

    @FXML
    private void saveNewInfo(){
        String username = SessionManager.getCurrentUser();
        dao.changeInfo(nomeLabelN.getText(), cognomeLabelN.getText(), telefonoLabelN.getText(), emailLabelN.getText(), username);
        infoUser();
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
}
