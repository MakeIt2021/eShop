package domain;

import java.util.List;
import java.util.Comparator;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;
import persistence.PersistenceManager;
import persistence.FilePersistenceManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class EShop {
    private String datei = "eShop";

    // TODO: Sollen sie alle final sein?
    private ArtikelVW artikelVW;
    private BenutzerVW benutzerVW = new BenutzerVW();
    private ArrayList<Ereignis> ereignisse;// Liste aller ereignisse
    private PersistenceManager pm;

    public BenutzerVW getBenutzerVW() {
        return benutzerVW;
    }

    private WarenkorbVW warenkorbVW;

    public EShop() throws IOException {
        this.datei = datei;
        artikelVW = new ArtikelVW();
        artikelVW.ladeArtikelMengeDaten(datei);
        warenkorbVW = new WarenkorbVW();
        ereignisse = new ArrayList<>();
        pm = new FilePersistenceManager();
        //pm.speichereEreignisArtikel(ereignisse);
        //ereignisse = pm.ladeEreignisse();
        ArrayList<Ereignis> geladen = pm.ladeEreignisse();
        ereignisse = new ArrayList<>();

        for (Ereignis e : geladen) {
            Artikel real = artikelVW.findeArtikel(e.getArtikel().getArtikelID());
            if (real != null) {
                ereignisse.add(new Ereignis(
                        e.getTag(),
                        real,
                        e.getMenge(),
                        e.getTyp(),
                        e.getPerson()
                ));
            } else {
                ereignisse.add(e);
            }
        }
    }
    public Map<Integer, Integer> berechneBestandHistorie(int artikelID) {
      /*  //methode fur ereignisse nach datum sortieren
        ereignisse.sort(Comparator.comparingInt(Ereignis::getMenge));*/

        // methode fur letzte 30 tage
        int heute = LocalDate.now().getDayOfYear();
        int startTag = heute - 30;

        //aktuele bestandsmenge
        int bestand = artikelVW.gibBestand(artikelID);

        Map<Integer, Integer> historie = new HashMap<>();

        // methode fur diesen artikel der letzte 30 tag
        List<Ereignis> events = ereignisse.stream()
                .filter(e -> e.getArtikel().getArtikelID() == artikelID)
                .filter(e -> e.getTag() >= startTag)
                .sorted(Comparator.comparingInt(Ereignis::getTag).reversed()) // rückwärts
                .toList();

        // ruckwarts durch der tag gehen
        for (int tag = heute; tag > startTag; tag--) {
            for (Ereignis e : events) {
                if (e.getTag() == tag) {
                    if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")) {
                        bestand -= e.getMenge();
                    } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                        bestand += e.getMenge();

                    }
                }
            }
            historie.put(tag, bestand);
        }
        return historie;

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

    //Gibt die Ereignisse zurück
    public ArrayList<Ereignis> gibEreignissen() {
        return ereignisse;
    }


    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, float preis, String mitarbeiter) throws IOException {
        Artikel art = new Artikel(artikelID, bezeichnung, preis);

        if (artikelVW.findeArtikel(artikelID) == null) {
            // NEUER ARTIKEL → in die Liste einfügen
            artikelVW.einfuegen(art, menge);
        } else {
            // Artikel existiert → Bestand erhöhen
            artikelVW.bestandErhoehen(artikelID, menge);
        }
        if (preis < 0) {
            throw new IllegalArgumentException(
                    "Preis darf nicht negativ sein."
            );
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("Bestand muss positiv sein.");
        }

        ereignisse.add(new Ereignis(
                LocalDate.now().getDayOfYear(),
                art,
                menge,
                "Einlagerung",
                "m:" + mitarbeiter
        ));

        pm.speichereEreignisArtikel(ereignisse);
        speichereArtikel();
    }
        /*Artikel art = new Artikel(artikelID, bezeichnung, preis);
        artikelVW.bestandErhoehen(artikelID, menge);
        if (preis < 0) {
            throw new IllegalArgumentException(
                    "Preis darf nicht negativ sein."
            );
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("Bestand muss positiv sein.");
        }

        ereignisse.add(new Ereignis(
                LocalDate.now().getDayOfYear(),
                art,
                menge,
                "Einlagerung",
                "m:" + mitarbeiter
        ));

        speichereArtikel();
    }*/

      /*  Artikel art = new Artikel(artikelID, bezeichnung, preis);
        artikelVW.einfuegen(art, menge);

        *//*
         * Ereignis erzeugen:
         * Einlagerung durch Mitarbeiter
         *//*
        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), art, menge, "Einlagerung", "m:" + mitarbeiter);
        ereignisse.add(ereignis);

        speichereArtikel();
// Exception für preis und mengefuegeInWarenkorb
        if (preis < 0) {
            throw new IllegalArgumentException(
                    "Preis darf nicht negativ sein."
            );
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("Bestand muss positiv sein.");
        }
    }*/

    public void artikelVernichten(int artikelID) throws IOException {
        artikelVW.artikelVernichten(artikelID);
        speichereArtikel();
    }

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) throws IOException {
        artikelVW.bezeichnungVeraendern(artikelID, bezeichnung);
        speichereArtikel();
    }

    public void preisVeraendern(int artikelID, float preis) throws IOException {
        artikelVW.preisVeraendern(artikelID, preis);
        speichereArtikel();

        //Exception
        if (preis < 0) {
            throw new IllegalArgumentException("Preis darf nicht negativ sein.");
        }
    }

    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws IOException {
        warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.bestandVerringern(artikelID, menge);

        ereignisse.add(new Ereignis(
                LocalDate.now().getDayOfYear(),
                artikelVW.findeArtikel(artikelID),
                menge,
                "Auslagerung",
                "k:" + kunde
        ));
        /*warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.loeschen(artikelID, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Auslagerung", "k:" + kunde);
        ereignisse.add(ereignis);*/

        speichereArtikel();

//Exception
        if (menge <= 0) {
            throw new IllegalArgumentException("Menge muss positiv sein.");
        }
        if (artikelVW.gibBestand(artikelID) < menge) {
            throw new IllegalStateException("Nicht genug Bestand.");
        }
    }

    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws IOException {
        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);

        Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), menge, "Einlagerung", "k:" + kunde);
        ereignisse.add(ereignis);

        speichereArtikel();
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
    public void speichereEreignisse()
            throws IOException {
        pm.speichereEreignisArtikel(ereignisse);
        speichereArtikel();
    }

    public void speichereArtikel() throws IOException {
        artikelVW.speichereArtikelMengeDaten(datei+"_AM.txt");
        artikelVW.speichereArtikelDaten(datei+"_A.txt");
    }

    public int sucheNachID(String bezeichnung) {
        return artikelVW.sucheNachIDMitBezeichnung(bezeichnung);
    }

    public void bestandVeraendern(int artikelID, int neuerBestand, String mitarbeiter) throws IOException {
        int aktuellerBestand = artikelVW.gibBestand(artikelID);
        if (aktuellerBestand < neuerBestand) {
            artikelVW.bestandErhoehen(artikelID, neuerBestand - aktuellerBestand);

            Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), neuerBestand - aktuellerBestand, "Einlagerung", "m:" + mitarbeiter);
            ereignisse.add(ereignis);
        } else {
            artikelVW.bestandVerringern(artikelID, aktuellerBestand - neuerBestand);

            Ereignis ereignis = new Ereignis(LocalDate.now().getDayOfYear(), artikelVW.findeArtikel(artikelID), aktuellerBestand - neuerBestand, "Auslagerung", "m:" + mitarbeiter);
            ereignisse.add(ereignis);
        }

        speichereArtikel();

        // Exception
        if (neuerBestand < 0) {
            throw new IllegalArgumentException("Ungültiger Bestand.");
        }
    }

    public boolean login (String benutzerErkennung, String benutzerPasswort) {
        return benutzerVW.login(benutzerErkennung, benutzerPasswort);

    }

    public void logout () {
        benutzerVW.logout();
    }

    public boolean istEingeloggt(){
        return benutzerVW.istEingeloggt();
    }

    public boolean istMitarbeiter(){
        return benutzerVW.istMitarbeiter();
    }

    public boolean istKunde(){
        return benutzerVW.istKunde();
    }

    public boolean registrieren (Benutzer benutzer){
        return benutzerVW.registrieren(benutzer);
    }

    public Benutzer aktuellerBenutzer () {
        return benutzerVW.getAktuellerBenutzer();
    }

    public Object gibBestand(int id) {
        return artikelVW.gibBestand(id);
    }
}
