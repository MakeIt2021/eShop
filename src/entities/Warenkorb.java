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

    /**
     * Entfernt eine Menge eines Artikels aus dem Warenkorb.
     * Wird die Menge 0 oder kleiner, wird der Artikel komplett entfernt.
     */
    public void loeschen(int artikelID, int menge) {
        int neueMenge = warenkorbMenge.get(artikelID) - menge;
        if (neueMenge <= 0) {
            warenkorbMenge.remove(artikelID);
        } else {
            warenkorbMenge.put(artikelID, neueMenge);
        }
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
