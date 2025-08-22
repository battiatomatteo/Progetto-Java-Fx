package enums;

/**
 * Enum stati terapia
 * @packege enums
 */
public enum StatoTerapia {
    ATTIVA,
    SOSPESA,
    TERMINATA;

    /**
     * Metodo con lo scopo di prendere lo stato
     * @return stato
     */
    public String getStato() {
        return this.name();
    }

}


