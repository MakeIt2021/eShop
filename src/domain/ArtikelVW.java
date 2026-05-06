package domain;

import entities.Artikel;
import java.util.HashMap;


public class ArtikelVW {
    private final HashMap<Integer, Artikel> artikelListe = new HashMap<>();
    private final HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();

    public void bestandErhoehen(int artikelID, int menge) {
        artikelMengeListe.put(artikelID, artikelMengeListe.get(artikelID) + menge);
    }

    public void bestandVerringern(int artikelID, int menge) {
        if (artikelMengeListe.get(artikelID) > 0)
            artikelMengeListe.put(artikelID, artikelMengeListe.get(artikelID) - menge);
    }

    public void einfuegen(Artikel einArtikel, int menge) {
        if (gibArtikelListe().containsKey(einArtikel.getArtikelID()))
            bestandErhoehen(einArtikel.getArtikelID(), menge);
        else {
            artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            artikelMengeListe.put(einArtikel.getArtikelID(), menge);
        }
    }

    public void loeschen(int artikelID, int menge) {
        bestandVerringern(artikelID, menge);
    }

    public Artikel artikelFinde(int artikelID) {
        return artikelListe.get(artikelID);
    }

    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelListe;
    }
    
    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelMengeListe;
    }
}
