package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pasto {

    private final StringProperty pasto;
    private final StringProperty orario;
    private final StringProperty pre;
    private final StringProperty post;

    public Pasto(String pasto, String orario, String pre, String post) {
        this.pasto = new SimpleStringProperty(pasto);
        this.orario = new SimpleStringProperty(orario);
        this.pre = new SimpleStringProperty(pre);
        this.post = new SimpleStringProperty(post);
    }

    public StringProperty pastoProperty() { return pasto; }
    public StringProperty orarioProperty() { return orario; }
    public StringProperty preProperty() { return pre; }
    public StringProperty postProperty() { return post; }

    public String getPasto() { return pasto.get(); }
    public void setPasto(String value) { pasto.set(value); }

    public String getOrario() { return orario.get(); }
    public void setOrario(String value) { orario.set(value); }

    public String getPre() { return pre.get(); }
    public void setPre(String value) { pre.set(value); }

    public String getPost() { return post.get(); }
    public void setPost(String value) { post.set(value); }
}
