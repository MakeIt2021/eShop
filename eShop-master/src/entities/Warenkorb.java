package entities;

import domain.WarenkorbVW;

import java.util.Date;
import java.util.HashMap;

public class Warenkorb {
    private Benutzer benutzer;
    private Date erzeugt; // fuer Erweiterung
    private HashMap<Integer, Integer> warenkorbMenge = new HashMap<>();

    public Warenkorb() {
//        this.benutzer = benutzer;
    }

    public void hinzufuegen(int artikelID, int menge) {
        if (warenkorbMenge.containsKey(artikelID)) {
            warenkorbMenge.put(artikelID, warenkorbMenge.get(artikelID) + menge);
        } else {
            warenkorbMenge.put(artikelID, menge);
        }
    }

    public void loeschen(int artikelID, int menge) {
        if (gibMenge(artikelID) > 0)
            warenkorbMenge.put(artikelID, warenkorbMenge.get(artikelID) - menge);
    }

    public int gibMenge(int artikelID) {
        return warenkorbMenge.get(artikelID);
    }

    public boolean containsArtikel(int artikelID) {
        return warenkorbMenge.containsKey(artikelID);
    }

    public void zuruecksetzen() {
        warenkorbMenge.clear();
    }

    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorbMenge;
    }
}
