package domain;

import entities.Artikel;
import entities.Ereignis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class EShop {
    private String datei = "eShop";

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
        artikelVW.ladeArtikelMengeDaten(datei);
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


    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, float preis, String mitarbeiter) {
        Artikel art = new Artikel(artikelID, bezeichnung, preis);
        artikelVW.einfuegen(art, menge);

        /*
         * Ereignis erzeugen:
         * Einlagerung durch Mitarbeiter
         */
        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), art, menge, "Einlagerung", mitarbeiter);
        ereignisse.add(ereignis);
    }

    public void loescheArtikel(int artikelID, int menge, String mitarbeiter) {
        artikelVW.loeschen(artikelID, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Auslagerung", mitarbeiter);
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

    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) {
        warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.loeschen(artikelID, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Auslagerung", kunde);
        ereignisse.add(ereignis);
    }

    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) {
        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Einlagerung", kunde);
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

    /*
     * Speichert alle Ereignisse in einer TXT-Datei
     */
    public void speichereEreignisseTXT() {

        try {

            // Datei erstellen
            PrintWriter writer = new PrintWriter(
                    new FileWriter("ereignisse.txt")
            );

            // Alle Ereignisse in Datei schreiben
            for (Ereignis e : ereignisse) {

                writer.println(e);
            }

            // Datei schließen
            writer.close();

            System.out.println("Ereignisse wurden gespeichert.");

        } catch (IOException e) {

            System.out.println("Fehler beim Speichern.");
        }
    }

    public void speichereArtikel() throws IOException {
        artikelVW.speichereArtikelMengeDaten(datei+"_AM.txt");
        artikelVW.speichereArtikelDaten(datei+"_A.txt");
    }
}
