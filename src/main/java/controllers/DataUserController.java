package controllers;

import DAO.PatientPaneDao;
import enums.StatoTerapia;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.ChartFilter;
import models.FilterDataSetter;
import models.Terapia;
import utility.SessionManager;
import utility.UIUtils;
import view.UserProfileView;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * Controller della finestra che permette la visione dei dati utente tramite una pagina dedicata.
 * @package controllers
 * @see DAO.PatientPaneDao
 * @see utility.UIUtils
 * @see <a href="https://github.com/battiatomatteo/Progetto-Java-Fx/blob/main/src/main/resources/fxml/DataUser.fxml">DataUser.fxml</a>
 */
public class DataUserController {

    // Attributi della classe
    @FXML private PatientChartController chartIncludeController;
    @FXML private TableView<Terapia> table;
    @FXML private TableColumn<Terapia, String> farmacoCol, assunzioniCol, quantFarCol, noteCol;
    @FXML private TableColumn<Terapia, Integer > terapiaCol;
    @FXML private TableColumn<Terapia, StatoTerapia> statoCol;
    @FXML private Button logOutButton, backb, provaAccount;
    @FXML private Label IntevalloLabel;
    @FXML private Button  settimanaSucc, settimanaPrec, meseSucc, mesePrec;
    /**
     * Oggetto per accesso al database
     * @see DAO.PatientPaneDao
     */
    private PatientPaneDao dao;
    private UIUtils daoU = new UIUtils();

    /**
     * Formato della data: anno-mese-giorno
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * Data formattata in base a DATE_FORMAT
     */
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);

    /**
     * Data attuale
     */
    private LocalDate dataAttuale = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    /**
     * Metodo di inizializzazione automatico eseguito al caricamento del controller.
     */
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

        backb.setOnAction(e -> {
            try {
                daoU.handleBack(SessionManager.getCurrentUser(), (Stage) backb.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        logOutButton.setOnAction(e -> UIUtils.LogOutButton((Stage) logOutButton.getScene().getWindow()));

        provaAccount.setOnAction(e -> {
            try {
                profilo((Stage) provaAccount.getScene().getWindow());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        settimanaPrec.setOnAction(event -> datiSettimanaPrecedente());
        settimanaSucc.setOnAction(event -> datiSettimanaSuccessiva());
        mesePrec.setOnAction(event -> datiMesePrecedente());
        meseSucc.setOnAction(event -> datiMeseSuccessivo());

        ricaricaDatiGrafico();
    }

    /**
     * Questo metodo ha lo scopo di cercare tutte le terapie dell'utente presenti nel database
     * @see DAO.PatientPaneDao
     */
    @FXML
    private void searchTerapie() {
        String username = SessionManager.getCurrentUser();

        FilterDataSetter filter = new FilterDataSetter(username,FilterDataSetter.ALL_STATUS_VIEWS,FilterDataSetter.ALL_THERAPY);
        table.setItems(dao.getTerapieList(filter));
        UIUtils.printMessage("inizializzazione in cerca");
        chartIncludeController.setData(username, new ChartFilter(ChartFilter.NO_START_DATE, ChartFilter.NO_END_DATE,ChartFilter.NO_ID )); // passo il nome del paziente
    }

    /**
     * Questo metodo ha lo scopo di settare il profilo utente
     * @param stage
     * @throws Exception
     */
    private void profilo(Stage stage) throws Exception {
        String username = SessionManager.getCurrentUser();
        new UserProfileView(username).start(stage);
    }

    /**
     * Questo metodo ha lo scopo di ricaricare il grafico con i dati attuali
     */
    private void ricaricaDatiGrafico(){
        String fine = fineSettimana();
        String oggi = dayToString(dataAttuale);
        IntevalloLabel.setText(intervalloLabelText(oggi, fine));
        ChartFilter filter = new ChartFilter(oggi, fine,ChartFilter.NO_ID );
        chartIncludeController.setData(SessionManager.getCurrentUser(), filter );
    }

    // questi metodi quando vengono richiamati modificano la data visualizzata e ricaricano il grafico
    private void datiSettimanaPrecedente() {
        dataAttuale = dataAttuale.minusWeeks(1);
        ricaricaDatiGrafico();
    }

    private void datiSettimanaSuccessiva() {
        dataAttuale = dataAttuale.plusWeeks(1);
        ricaricaDatiGrafico();
    }

    private void datiMeseSuccessivo() {
        dataAttuale = dataAttuale.plusMonths(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ricaricaDatiGrafico();
    }

    private void datiMesePrecedente() {
        dataAttuale = dataAttuale.minusMonths(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ricaricaDatiGrafico();
    }

    private String fineSettimana(){
        LocalDate fineSettimana =  dataAttuale.plusDays(6);
        return dayToString(fineSettimana);
    }

    /**
     * Data a stringa
     * @param day
     * @return String - giorno in stringa
     */
    private String dayToString(LocalDate day){
        return day.format(dateFormat);
    }

    /**
     * Costruisce la label coi giorni dell'intervallo mostrati
     * @param startDay giorno inizio
     * @param endDay giorno fine
     * @return String - intervallo giorni
     */
    private String intervalloLabelText(String startDay, String endDay){
        return startDay + " - " + endDay;
    }
}
