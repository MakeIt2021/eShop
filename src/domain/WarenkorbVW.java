package domain;

import entities.Warenkorb;
import java.util.HashMap;


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
