package controllers;

import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.ChartDataSetter;
import models.FilterDataSetter;
import utility.UIUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class SceltaOpzioneController {
    @FXML private ComboBox<String> FarmacoComboBox;
    @FXML private CheckBox check1;
    @FXML private CheckBox check2;
    @FXML private CheckBox check3;

    private int valoreStatoSelezionato = 0;
    private String valoreFarmacoSelezionato = null;
    private String username = null;

    @FXML
    public void initialize() {
        check1.setText(StatoTerapia.ATTIVA.getStato());
        check2.setText(StatoTerapia.SOSPESA.getStato());
        check3.setText(StatoTerapia.TERMINATA.getStato());
        check1.setUserData(FilterDataSetter.ON_GOING_STATUS);
        check2.setUserData(FilterDataSetter.ON_PAUSE_STATUS);
        check3.setUserData(FilterDataSetter.TERMINATED_STATUS);
    }

    public int getValoreStatoSelezionato() {
        return valoreStatoSelezionato;
    }

    public String getValoreFarmacoSelezionato() {
        return valoreFarmacoSelezionato;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWindowsData() {
        ArrayList<String> farmaci = UIUtils.getFarmaciPaziente(username);
        FarmacoComboBox.getItems().addAll(farmaci);
    }

    @FXML
    private void confermaScelta(javafx.event.ActionEvent event) {
        valoreStatoSelezionato = 0;
        if (check1.isSelected()) valoreStatoSelezionato += FilterDataSetter.ON_GOING_STATUS;
        if (check2.isSelected()) valoreStatoSelezionato += FilterDataSetter.ON_PAUSE_STATUS;
        if (check3.isSelected()) valoreStatoSelezionato += FilterDataSetter.TERMINATED_STATUS;
        valoreFarmacoSelezionato = FarmacoComboBox.getValue();
        // Chiude la finestra
        Stage stage = (Stage) FarmacoComboBox.getScene().getWindow();
        stage.close();
    }

}
