package models;

import enums.StatoTerapia;
import javafx.beans.property.*;

/**
 * Questa classe rappresenta una terapia assegnata a un paziente.
 * Contiene informazioni sul farmaco, la quantità, le modalità di assunzione, note aggiuntive
 * e lo stato della terapia (attiva, sospesa, terminata).
 *
 * @package models
 * @see enums.StatoTerapia
 */
public class Terapia {

    // Attributi della classe
    private final IntegerProperty id_terapia;
    private final StringProperty farmaco;
    private final StringProperty assunzioni;
    private final StringProperty quantFarmaco;
    private final StringProperty note;
    private final ObjectProperty<StatoTerapia> stato;

    /**
     * Costruttore della classe Terapia.
     * Crea una nuova istanza della terapia con i dati forniti. Gestisce eventuali errori
     * di conversione dello stato con un valore di default (SOSPESA).
     *
     * @param id_terapia   identificativo della terapia
     * @param stato        stato della terapia come stringa (verrà convertito in enum)
     * @param farmaco      nome del farmaco prescritto
     * @param assunzioni   orari o modalità di assunzione
     * @param quantFarmaco quantità del farmaco
     * @param note         eventuali note aggiuntive
     */
    public Terapia(int id_terapia, String stato, String farmaco, String assunzioni, String quantFarmaco, String note) {
        this.id_terapia = new SimpleIntegerProperty(id_terapia);
        this.farmaco = new SimpleStringProperty(farmaco);
        this.assunzioni = new SimpleStringProperty(assunzioni);
        this.quantFarmaco = new SimpleStringProperty(quantFarmaco);
        this.note = new SimpleStringProperty(note);
        //this.stato = new SimpleObjectProperty<>(StatoTerapia.valueOf(stato));

        //  per prevenire eccezioni nel caso in cui nel database venga salvata per errore una stringa non valida
        StatoTerapia statoEnum;
        try {
            statoEnum = StatoTerapia.valueOf(stato.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            statoEnum = StatoTerapia.SOSPESA; // valore di default se stringa non valida
            System.err.println("Stato non valido per terapia ID " + id_terapia + ": \"" + stato + "\". Usato valore di default SOSPESA.");
        }
        this.stato = new SimpleObjectProperty<>(statoEnum);
    }

    /**
     * Restituisce l'ID della terapia.
     * @return int - id terapia
     */
    public int getIdTerapia() { return id_terapia.get(); }

    /**
     * Restituisce il nome del farmaco.
     * @return String - nome del farmaco
     */
    public String getFarmaco() { return farmaco.get(); }

    /**
     * Restituisce le assunzioni (orari/modalità).
     * @return String - assunzioni
     */
    public String getAssunzioni() { return assunzioni.get(); }

    /**
     * Restituisce la quantità del farmaco.
     * @return String - quantità
     */
    public String getQuantita() { return quantFarmaco.get(); }

    /**
     * Restituisce le note aggiuntive associate alla terapia.
     * @return String - note
     */
    public String getNote() { return note.get(); }

    /**
     * Restituisce lo stato attuale della terapia come enum.
     * @return StatoTerapia - stato terapia
     */
    public StatoTerapia getStatoEnum() { return stato.get(); }

    /**
     * Property per l'ID terapia (per JavaFX binding).
     * @return IntegerProperty
     */
    public IntegerProperty idTerapiaProperty() { return id_terapia; }

    /**
     * Property per il nome del farmaco (per JavaFX binding).
     * @return StringProperty
     */
    public StringProperty farmacoProperty() { return farmaco; }

    /**
     * Property per le assunzioni (per JavaFX binding).
     * @return StringProperty
     */
    public StringProperty assunzioniProperty() { return assunzioni; }

    /**
     * Property per la quantità del farmaco (per JavaFX binding).
     * @return StringProperty
     */
    public StringProperty quantitaProperty() { return quantFarmaco; }

    /**
     * Property per le note (per JavaFX binding).
     * @return StringProperty
     */
    public StringProperty noteProperty() { return note; }

    /**
     * Property per lo stato della terapia (per JavaFX binding).
     * @return ObjectProperty<StatoTerapia>
     */
    public ObjectProperty<StatoTerapia> statoEnumProperty() { return stato; }

    /**
     * Imposta un nuovo stato alla terapia.
     * @param stato - nuovo stato da assegnare
     */
    public void setStato(StatoTerapia stato) {
        this.stato.set(stato);
    }
}
