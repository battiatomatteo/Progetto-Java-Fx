package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pasto {

    private final StringProperty pasto;
    private final StringProperty pre;
    private final StringProperty post;
    private final StringProperty orario;

    public Pasto(String pasto, String orario, String pre, String post) {
        this.pasto = new SimpleStringProperty(pasto);
        this.orario = new SimpleStringProperty(orario);
        this.pre = new SimpleStringProperty(pre);
        this.post = new SimpleStringProperty(post);
    }

    public StringProperty pastoProperty() {
        return pasto;
    }

    public StringProperty preProperty() {
        return pre;
    }

    public StringProperty postProperty() {
        return post;
    }

    public StringProperty orarioProperty() {
        return orario;
    }

    public String getPasto() {
        return "";
    }
}
