package domain;

import entities.Artikel;
import entities.Warenkorb;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class WarenkorbVW {
//    private HashMap<Integer, Integer> warenkorbMenge = new HashMap<>();
    // Warenkorb warenkorb = new Warenkorb(Benutzer); // TODO: eigentlich sollte hier Benutzer sein
    Warenkorb warenkorb = new Warenkorb();

    public void einfuegen(int artikelID, int menge) {
        warenkorb.hinzufuegen(artikelID, menge);
    }

    public void loeschen(int artikelID, int menge) {
        warenkorb.loeschen(artikelID, menge);
    }

    public void zuruecksetzen() {
        warenkorb.zuruecksetzen();
    }

    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorb.gibWarenkorb();
    }
}
