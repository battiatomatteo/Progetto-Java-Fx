package models;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pasto {

    private final StringProperty pasto;
    private final StringProperty orario;
    private final FloatProperty pre;
    private final FloatProperty post;

    public Pasto(String pasto, String orario, float pre, float post) {
        this.pasto = new SimpleStringProperty(pasto);
        this.orario = new SimpleStringProperty(orario);
        this.pre = new SimpleFloatProperty(pre);
        this.post = new SimpleFloatProperty(post);
    }

    public StringProperty pastoProperty() { return pasto; }
    public StringProperty orarioProperty() { return orario; }
    public FloatProperty preProperty() { return pre; }
    public FloatProperty postProperty() { return post; }

    public String getPasto() { return pasto.get(); }
    public void setPasto(String value) { pasto.set(value); }

    public String getOrario() { return orario.get(); }
    public void setOrario(String value) { orario.set(value); }

    public float getPre() { return pre.get(); }
    public void setPre(float value) { pre.set(value); }

    public float getPost() { return post.get(); }
    public void setPost(float value) { post.set(value); }
}
