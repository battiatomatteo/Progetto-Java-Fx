package models;

/**
 * Questa classe rappresenta una rilevazione effettuata dal paziente in una data specifica.
 * Contiene i valori di glicemia pre-pasto e post-pasto.
 *
 * @package models
 */
public class Rilevazioni {

    // Attributi della classe
    /**
     * Data della rilevazione (formato stringa)
     */
    private final String date;
    /**
     * Valore della glicemia prima del pasto
     */
    private final float rilevazionePrePasto;
    /**
     * Valore della glicemia dopo il pasto
     */
    private final float rilevazionePostPasto;

    /**
     * Costruttore della classe Rilevazioni.
     * Crea una nuova istanza con data, valore pre-pasto e valore post-pasto.
     *
     * @param date                   data della rilevazione
     * @param rilevazionePrePasto   valore della glicemia prima del pasto
     * @param rilevazionePostPasto  valore della glicemia dopo il pasto
     */
    public Rilevazioni(String date, float rilevazionePrePasto, float rilevazionePostPasto) {
        this.date = date;
        this.rilevazionePrePasto = rilevazionePrePasto;
        this.rilevazionePostPasto = rilevazionePostPasto;
    }

    /**
     * Restituisce la data della rilevazione.
     *
     * @return String - data
     */
    public String getDate() {
        return date;
    }

    /**
     * Restituisce il valore di glicemia prima del pasto.
     *
     * @return float - valore pre-pasto
     */
    public float getRilevazionePrePasto() {
        return rilevazionePrePasto;
    }

    /**
     * Restituisce il valore di glicemia dopo il pasto.
     *
     * @return float - valore post-pasto
     */
    public float getRilevazionePostPasto() {
        return rilevazionePostPasto;
    }
}
