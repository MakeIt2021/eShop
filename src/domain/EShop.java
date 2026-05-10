package domain;

import entities.Artikel;
import java.io.IOException;
import java.util.HashMap;


public class EShop {
    private String datei = "";

    private ArtikelVW artikelVW;

    private WarenkorbVW warenkorbVW;

    public EShop() throws IOException {
        this.datei = datei;
        artikelVW = new ArtikelVW();
        warenkorbVW = new WarenkorbVW();
    }

    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelVW.gibArtikelListe();
    }

    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelVW.gibArtikelMengeListe();
    }

    public HashMap<Integer, Integer> gibWarenkorb() {

        return warenkorbVW.gibWarenkorb();
    }

    public void fuegeArtikelEin(int artikelID, String bezeichnung, int bestand, int preis) {
        Artikel art = new Artikel(artikelID, bezeichnung, preis);
        artikelVW.einfuegen(art, bestand);
    }

    public void loescheArtikel(int artikelID, int menge) {
        System.out.println(artikelID);
        artikelVW.loeschen(artikelID, menge);
    }

    public void fuegeInWarenkorb(int artikelID, int menge) {
        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.loeschen(artikelID, menge);
    }

    public void loescheAusWarenkorb(int artikelID, int menge) {
        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);
    }


    public void seedTestData() {
        fuegeArtikelEin(1, "Laptop", 999, 10);
        fuegeArtikelEin(2, "Mouse", 25, 50);
        fuegeArtikelEin(3, "Keyboard", 70, 30);
        fuegeArtikelEin(4, "Monitor", 200, 15);
        fuegeArtikelEin(5, "USB Cable", 5, 100);
        fuegeArtikelEin(6, "Headphones", 120, 25);
        fuegeArtikelEin(7, "Webcam", 80, 20);
        fuegeArtikelEin(8, "Chair", 150, 10);
        fuegeArtikelEin(9, "Desk", 300, 5);
        fuegeArtikelEin(10, "SSD 1TB", 110, 40);
    }
}
