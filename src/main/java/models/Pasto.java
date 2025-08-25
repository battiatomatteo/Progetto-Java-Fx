package models;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Questa classe rappresenta un pasto giornaliero registrato per un paziente.
 * Contiene informazioni sul nome del pasto (es. colazione, pranzo, cena),
 * l'orario in cui è stato consumato e le rilevazioni di glicemia
 * effettuate prima e dopo il pasto.
 *
 * @package models
 */
public class Pasto {

    // Attributi della classe
    /**
     * Nome del pasto (es. "Colazione", "Pranzo", "Cena").
     */
    private final StringProperty pasto;

    /**
     * Orario del pasto (formato consigliato: HH:mm).
     */
    private final StringProperty orario;

    /**
     * Valore di glicemia rilevato prima del pasto.
     */
    private final FloatProperty pre;

    /**
     * Valore di glicemia rilevato dopo il pasto.
     */
    private final FloatProperty post;

    // Metodi della classe
    /**
     * Costruttore della classe Pasto.
     * Inizializza un nuovo pasto con nome, orario e rilevazioni di glicemia.
     *
     * @param pasto nome del pasto (es. "Pranzo")
     * @param orario orario in cui è stato consumato il pasto
     * @param pre valore di glicemia misurato prima del pasto
     * @param post valore di glicemia misurato dopo il pasto
     */
    public Pasto(String pasto, String orario, float pre, float post) {
        this.pasto = new SimpleStringProperty(pasto);
        this.orario = new SimpleStringProperty(orario);
        this.pre = new SimpleFloatProperty(pre);
        this.post = new SimpleFloatProperty(post);
    }

    /**
     * Property per il nome del pasto.
     * @return StringProperty
     */
    public StringProperty pastoProperty() { return pasto; }

    /**
     * Property per l'orario del pasto.
     * @return StringProperty
     */
    public StringProperty orarioProperty() { return orario; }

    /**
     * Property per la glicemia pre-pasto.
     * @return FloatProperty
     */
    public FloatProperty preProperty() { return pre; }

    /**
     * Property per la glicemia post-pasto.
     * @return FloatProperty
     */
    public FloatProperty postProperty() { return post; }

    // Getter e Setter
    /**
     * Restituisce il nome del pasto.
     * @return String - nome del pasto
     */
    public String getPasto() { return pasto.get(); }

    /**
     * Imposta un nuovo nome per il pasto.
     * @param value nuovo nome del pasto
     */
    public void setPasto(String value) { pasto.set(value); }

    /**
     * Restituisce l'orario del pasto.
     * @return String - orario
     */
    public String getOrario() { return orario.get(); }

    /**
     * Imposta un nuovo orario per il pasto.
     * @param value nuovo orario del pasto
     */
    public void setOrario(String value) { orario.set(value); }

    /**
     * Restituisce il valore di glicemia rilevato prima del pasto.
     * @return float - glicemia pre-pasto
     */
    public float getPre() { return pre.get(); }

    /**
     * Imposta un nuovo valore di glicemia pre-pasto.
     * @param value glicemia pre-pasto
     */
    public void setPre(float value) { pre.set(value); }

    /**
     * Restituisce il valore di glicemia rilevato dopo il pasto.
     * @return float - glicemia post-pasto
     */
    public float getPost() { return post.get(); }

    /**
     * Imposta un nuovo valore di glicemia post-pasto.
     * @param value glicemia post-pasto
     */
    public void setPost(float value) { post.set(value); }
}
