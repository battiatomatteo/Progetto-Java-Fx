package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Day {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final ArrayList<Pasto> pasti = new ArrayList<>();
    private final LocalDate data;

    public Day(LocalDate data){
        this.data = data;
    }
    private boolean check(Pasto p){
        return checkField(p.getPost()) && checkField(p.getPre()) && checkField(p.getOrario());
    }
    private boolean checkField(float f){
        return f > 0;
    }
    private boolean checkField(String f){
        return !(f == null || f.isEmpty());
    }
    public ArrayList<Pasto> getPasti() {
        return pasti;
    }
    public LocalDate getData() {
        return data;
    }
    public String getDataString(){
        return LocalDate.now().format(formatter);
    }
    public void addPasto(Pasto p) {
        if(check(p)){
            pasti.add(p);
        }
    }

}
