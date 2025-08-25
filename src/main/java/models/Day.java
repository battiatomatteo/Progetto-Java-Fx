package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Classe che rappresenta le rilevazioni di oggi
 */
public class Day {
    // Costanti definite per la classe
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


     // Attributi della classe
    /**
     * Lista dei pasti del giorno
     */
    private final ArrayList<Pasto> pasti = new ArrayList<>();

    // Metodi della classe
    /**
     * Costruttore della classe
     */
    public Day(){
    }

    /**
     * Questo metodo ha lo scopo di valutare se il pasto p è valido
     * @param p Pasto da valutare
     * @return Esito della valutazione
     */
    private boolean check(Pasto p){
        return checkField(p.getPost()) && checkField(p.getPre()) && checkField(p.getOrario());
    }

    /**
     * Questo metodo ha lo scopo di valutare se il valore f è accettabile
     * @param f Valore da controllare
     * @return Esito della valutazione
     */
    private boolean checkField(float f){
        return f > 0;
    }

    /**
     * Questo metodo ha lo scopo di valutare se il valore f è accettabile
     * @param f Valore da controllare
     * @return Esito della valutazione
     */
    private boolean checkField(String f){
        return !(f == null || f.isEmpty());
    }

    /**
     * Questo metodo restituisce la lista dei pasti di oggi
     * @return
     */
    public ArrayList<Pasto> getPasti() {
        return pasti;
    }

    /**
     * Questo metodo restituisce la data di oggi
     * @return
     */
    public String getDataString(){
        return LocalDate.now().format(formatter);
    }

    /**
     * Questo metodo ha lo scopo di aggiunge un pasto alla lista dei pasti di oggi
     * @param p Pasto da aggiungere
     */
    public void addPasto(Pasto p) {
        if(check(p)){
            pasti.add(p);
        }
    }

}
