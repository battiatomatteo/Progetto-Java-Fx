package models;

import enums.StatoTerapia;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Terapia {
    private final StringProperty id_terapia;
    private final StringProperty farmaco;
    private final StringProperty assunzioni;
    private final StringProperty quantFarmaco;
    private final StringProperty note;
    private final ObjectProperty<StatoTerapia> stato;

    public Terapia(String id_terapia, String stato, String farmaco, String assunzioni, String quantFarmaco, String note) {
        this.id_terapia = new SimpleStringProperty(id_terapia);
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
            statoEnum = StatoTerapia.ATTESA; // valore di default se stringa non valida
            System.err.println("⚠️ Stato non valido per terapia ID " + id_terapia + ": \"" + stato + "\". Usato valore di default ATTESA.");
        }
        this.stato = new SimpleObjectProperty<>(statoEnum);
    }

    public String getIdTerapia() { return id_terapia.get(); }
    public String getFarmaco() { return farmaco.get(); }
    public String getAssunzioni() { return assunzioni.get(); }
    public String getQuantita() { return quantFarmaco.get(); }
    public String getNote() { return note.get(); }
    public StatoTerapia getStatoEnum() { return stato.get(); }

    public StringProperty idTerapiaProperty() { return id_terapia; }
    public StringProperty farmacoProperty() { return farmaco; }
    public StringProperty assunzioniProperty() { return assunzioni; }
    public StringProperty quantitaProperty() { return quantFarmaco; }
    public StringProperty noteProperty() { return note; }
    public ObjectProperty<StatoTerapia> statoEnumProperty() { return stato; }

    public void setStato(StatoTerapia stato) {
        this.stato.set(stato);
    }
}
