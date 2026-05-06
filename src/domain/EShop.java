package domain;

import entities.Artikel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class EShop {
    // Präfix für Namen der Dateien, in der die Bibliotheksdaten gespeichert sind
    private String datei = "";

    private ArtikelVW artikelVW;

    // private KundenVerwaltung kundenVW;
    // hier weitere Verwaltungsklassen, z.B. für Autoren oder Angestellte


    public EShop() throws IOException {
        this.datei = datei;
        artikelVW = new ArtikelVW();

    }

    public HashMap<Integer, Artikel> gibAlleArtikel() {
        return artikelVW.getArtikelBestand();
    }


    public void fuegeArtikelEin(int artikelID, String bezeichnung, int bestand, int preis) {
        Artikel art = new Artikel(artikelID, bezeichnung, bestand, preis);
        artikelVW.einfuegen(art);
    }

    public void loescheArtikel(int artikelID) {
//        Artikel art = new Artikel(nummer, bezeichnung, bestand, preis);
        artikelVW.loeschen(artikelID);
    }
}
