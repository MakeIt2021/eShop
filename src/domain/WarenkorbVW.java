package domain;

import entities.Artikel;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class WarenkorbVW {
    private HashMap<Integer, Integer> warenkorbMenge = new HashMap<>();

    public void mengeErhoehen(int artikelID, int menge) {
        warenkorbMenge.put(artikelID, warenkorbMenge.get(artikelID) + menge);
    }

    public void mengeVerringern(int artikelID, int menge) {
        if (warenkorbMenge.get(artikelID) > 0)
            warenkorbMenge.put(artikelID, warenkorbMenge.get(artikelID) - menge);
    }

    public void einfuegen(int artikelID, int menge) {
        if (warenkorbMenge.containsKey(artikelID))
            mengeErhoehen(artikelID, menge);
        else
            warenkorbMenge.put(artikelID, menge);
    }

    public void loeschen(int artikelID, int menge) {
        mengeVerringern(artikelID, menge);
    }

    public void zuruecksetzen() {
        warenkorbMenge.clear();
    }

    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorbMenge;
    }
}
