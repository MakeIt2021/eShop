package domain;

import entities.Artikel;
import entities.Ereignis;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class EShop {
    private String datei = "";

    // TODO: Sollen sie alle final sein?
    private ArtikelVW artikelVW;
    private BenutzerVW benutzerVW = new BenutzerVW();
    private ArrayList<Ereignis> ereignisse; // Liste aller ereignisse
    public BenutzerVW getBenutzerVW() {
        return benutzerVW;
    }

    public ArtikelVW getArtikelVW() {
        return artikelVW;
    }

    private WarenkorbVW warenkorbVW;

    public EShop() throws IOException {
        this.datei = datei;
        artikelVW = new ArtikelVW();
        warenkorbVW = new WarenkorbVW();
        ereignisse = new ArrayList<>();
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
    // Gibt die Ereignisse zurück
    public ArrayList<Ereignis> gibEreignissen(){
        return ereignisse;
    }


    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, float preis) {
        Artikel art = new Artikel(artikelID, bezeichnung, preis);
        artikelVW.einfuegen(art, menge);

        /*
         * Ereignis erzeugen:
         * Einlagerung durch Mitarbeiter
         */
        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), art, menge, "Einlagerung", "Placeholder");
        ereignisse.add(ereignis);
    }

    public void loescheArtikel(int artikelID, int menge) {
        artikelVW.loeschen(artikelID, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Auslagerung", "Placeholder");
        ereignisse.add(ereignis);
    }

    public void artikelVernichten(int artikelID) {
        artikelVW.artikelVernichten(artikelID);
    }

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) {
        artikelVW.bezeichnungVeraendern(artikelID, bezeichnung);
    }

    public void preisVeraendern(int artikelID, float preis) {
        artikelVW.preisVeraendern(artikelID, preis);
    }

    public void fuegeInWarenkorb(int artikelID, int menge) {
        warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.loeschen(artikelID, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Auslagerung", "Placeholder");
        ereignisse.add(ereignis);
    }

    public void loescheAusWarenkorb(int artikelID, int menge) {
        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Einlagerung", "Placeholder");
        ereignisse.add(ereignis);
    }

    public void zuruecksetzeWarenkorb() {
        warenkorbVW.zuruecksetzen();
    }

    /*
     * Gibt alle Ereignisse aus
     */
    public void gibEreignisseAus() {

        // Prüfen ob Liste leer ist
        if (ereignisse.isEmpty()) {
            System.out.println("Keine Ereignisse vorhanden.");
        } else {
            // Alle Ereignisse ausgeben
            for (Ereignis e : ereignisse) {
                System.out.println(e);
            }
        }
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
