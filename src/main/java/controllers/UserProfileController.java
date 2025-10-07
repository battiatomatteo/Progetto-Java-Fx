package controllers;

import DAO.UserProfileDao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.SessionManager;

import java.util.ArrayList;

public class UserProfileController {

    @FXML private Label nomeLabel;
    @FXML private Label cognomeLabel;
    @FXML private Label emailLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label tipoUtenteLabel, infoLabel;
    @FXML private ImageView profileImage;

    private UserProfileDao dao = new UserProfileDao();

    @FXML
    public void initialize() {
        // Esempio dati utente


        // Immagine profilo
        Image image = new Image("/img/unnamed.jpg");
        profileImage.setImage(image);

        infoUser();
    }

    @FXML
    private void handleLogout() {
        // TODO: logica di logout
    }

    @FXML
    private void handleEdit() {
        // TODO: apri form modifica profilo
    }

    private void infoUser(){

        ArrayList<String> list = dao.caricoInfoUtente("p");

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
