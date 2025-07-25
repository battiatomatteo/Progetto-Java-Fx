package enums;

public enum StatoTerapia {
    ATTIVA,
    SOSPESA,
    TERMINATA;

    public String getStato() {
        return this.name();
    }
}


