package enums;

public enum StatoTerapia {
    ATTIVA,
    SOSPESA,
    TERMINATA;

    public String getStato() {
        return this.name();
    }

   public static String[] getAll() {
       String[] values = new String[StatoTerapia.values().length];
       for (StatoTerapia s : StatoTerapia.values()) {
           values[s.ordinal()] = s.name();
       }
       return values;
   }

}


