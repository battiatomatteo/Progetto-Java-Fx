package controllers;

import DAO.PatientPaneDao;
import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Circle;
import models.ChartFilter;
import models.FilterDataSetter;
import models.Terapia;
import utility.SessionManager;
import utility.UIUtils;

public class DataUserController {

    private String profiloUsername;
    @FXML private PatientChartController chartIncludeController;
    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private TableColumn<Terapia, Integer > terapiaCol;
    @FXML private TableColumn<Terapia, StatoTerapia> statoCol;
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientPaneDao
     */
    private PatientPaneDao dao;

    @FXML
    private void initialize() {
        terapiaCol.setCellValueFactory(cell -> cell.getValue().idTerapiaProperty().asObject());
        statoCol.setCellValueFactory(cell -> cell.getValue().statoEnumProperty());
        farmacoCol.setCellValueFactory(cell -> cell.getValue().farmacoProperty());
        assunzioniCol.setCellValueFactory(cell -> cell.getValue().assunzioniProperty());
        quantFarCol.setCellValueFactory(cell -> cell.getValue().quantitaProperty());
        noteCol.setCellValueFactory(cell -> cell.getValue().noteProperty());
        dao = new PatientPaneDao();

        statoCol.setCellFactory(column -> new TableCell<>() {
            private final Circle circle = new Circle(6);

            protected void updateItem(StatoTerapia stato, boolean empty) {
                super.updateItem(stato, empty);
                if (empty || stato == null) {
                    setGraphic(null);
                } else {
                    switch (stato) {
                        case ATTIVA -> circle.setStyle("-fx-fill: green;");
                        case SOSPESA -> circle.setStyle("-fx-fill: orange;");
                        case TERMINATA -> circle.setStyle("-fx-fill: red;");
                    }
                    setGraphic(circle);
                }
            }
        });
        searchTerapie();
    }

    @FXML
    private void searchTerapie() {
        String username = SessionManager.getCurrentUser();

        FilterDataSetter filter = new FilterDataSetter(username,FilterDataSetter.ALL_STATUS_VIEWS,FilterDataSetter.ALL_THERAPY);
        table.setItems(dao.getTerapieList(filter));
        UIUtils.printMessage("inizializzazione in cerca");
        chartIncludeController.setData(username, new ChartFilter(ChartFilter.NO_START_DATE, ChartFilter.NO_END_DATE,ChartFilter.NO_ID )); // passo il nome del paziente

    }
}
