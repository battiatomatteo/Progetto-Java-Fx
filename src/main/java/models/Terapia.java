package models;
// package com.dashapp.diabeticsystem.models;

import javafx.beans.property.SimpleStringProperty;

public class Terapia {
    private final SimpleStringProperty id_terapia;
    private final SimpleStringProperty farmaco;
    private final SimpleStringProperty assunzioni;
    private final SimpleStringProperty quantFarmaco;
    private final SimpleStringProperty note;

    public Terapia(String id_terapia, String farmaco, String assunzioni, String quantFarmaco, String note) {
        this.id_terapia = new SimpleStringProperty(id_terapia);
        this.farmaco = new SimpleStringProperty(farmaco);
        this.assunzioni = new SimpleStringProperty(assunzioni);
        this.quantFarmaco = new SimpleStringProperty(quantFarmaco);
        this.note = new SimpleStringProperty(note);
    }

    public String getIdTerapia() { return id_terapia.get(); }
    public String getFarmaco() { return farmaco.get(); }
    public String getAssunzioni() { return assunzioni.get(); }
    public String getQuantita() { return quantFarmaco.get(); }
    public String getNote() { return note.get(); }
    public SimpleStringProperty idTerapiaProperty() { return id_terapia; }
    public SimpleStringProperty farmacoProperty() { return farmaco; }
    public SimpleStringProperty assunzioniProperty() { return assunzioni; }
    public SimpleStringProperty quantitaProperty() { return quantFarmaco; }
    public SimpleStringProperty noteProperty() { return note; }
}