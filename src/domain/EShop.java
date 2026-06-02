package domain;

import domain.exceptions.*;
import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;
import entities.Massengutartikel;
import persistence.PersistenceManager;
import persistence.FilePersistenceManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


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
        ereignisse = pm.ladeEreignisse();

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
    public void fuegeArtikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            float preis,
            String mitarbeiter
    ) throws IOException {

        if (preis < 0) {
            throw new UngueltigerPreisException(preis);
        }

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        Artikel art = new Artikel(
                artikelID,
                bezeichnung,
                preis
        );

        artikelVW.einfuegen(art, menge);

        Ereignis ereignis = new Ereignis(
                LocalDate.now().getDayOfYear(),
                art,
                menge,
                "Einlagerung",
                "m:" + mitarbeiter
        );

        ereignisse.add(ereignis);

        speichereArtikel();
    }*/

    public void fuegeMassengutartikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            float preis,
            String mitarbeiter,
            int packungGroesse
    ) throws IOException {

        if (preis < 0) {
            throw new UngueltigerPreisException(preis);
        }

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (menge < packungGroesse) {
            throw new MengeWenigerAlsPackungGroesseException(
                    bezeichnung,
                    menge,
                    packungGroesse
            );
        }

        if (menge % packungGroesse != 0) {
            throw new MassengutartikelmengeNichtTeilbarException(
                    bezeichnung,
                    packungGroesse
            );
        }


        Artikel art = new Massengutartikel(
                artikelID,
                bezeichnung,
                preis,
                packungGroesse
        );

        artikelVW.einfuegen(art, menge);

        Ereignis ereignis = new Ereignis(
                LocalDate.now().getDayOfYear(),
                art,
                menge,
                "Einlagerung",
                "m:" + mitarbeiter
        );

        ereignisse.add(ereignis);

        speichereArtikel();
    }

    public void artikelVernichten(int artikelID) throws IOException {
        artikelVW.artikelVernichten(artikelID);
        speichereArtikel();
    }

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) throws IOException {
        artikelVW.bezeichnungVeraendern(artikelID, bezeichnung);
        speichereArtikel();
    }

    public void preisVeraendern(int artikelID, float preis) throws IOException {

        if (preis < 0) {
            throw new UngueltigerPreisException(preis);
        }

        artikelVW.preisVeraendern(artikelID, preis);
        speichereArtikel();
    }

    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws IOException {

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (artikelVW.getBestand(artikelID) < menge) {
            throw new BestandNichtAusreichendException(
                    getArtikelName(artikelID),
                    artikelVW.getBestand(artikelID),
                    menge
            );
        }

        if (istMassengutartikel(artikelID)) {

            if (menge < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        getArtikelName(artikelID),
                        menge,
                        getPackungGroesse(artikelID)
                );
            }

            if (menge % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        getArtikelName(artikelID),
                        getPackungGroesse(artikelID)
                );
            }
        }

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
        Ereignis ereignis = new Ereignis(
                LocalDate.now().getDayOfYear(),
                artikelVW.findeArtikel(artikelID),
                menge,
                "Auslagerung",
                "k:" + kunde
        );

        ereignisse.add(ereignis);

        speichereArtikel();
    }

    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws IOException {
        if (istMassengutartikel(artikelID)) {
            if (menge < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(getArtikelName(artikelID), menge, getPackungGroesse(artikelID));
            }

            if (menge % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(getArtikelName(artikelID), getPackungGroesse(artikelID));
            }
        }

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

        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID == -1) {
            throw new ArtikelExistiertNichtException(bezeichnung);
        }
        return artikelID;
    }

    public void bestandVeraendern(
            int artikelID,
            int neuerBestand,
            String mitarbeiter
    ) throws IOException {

        if (neuerBestand <= 0) {
            throw new UngueltigeMengeException(neuerBestand);
        }

        if (istMassengutartikel(artikelID)) {

            if (neuerBestand < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        getArtikelName(artikelID),
                        neuerBestand,
                        getPackungGroesse(artikelID)
                );
            }

            if (neuerBestand % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        getArtikelName(artikelID),
                        getPackungGroesse(artikelID)
                );
            }
        }

        int aktuellerBestand = artikelVW.getBestand(artikelID);

        if (aktuellerBestand < neuerBestand) {

            artikelVW.bestandErhoehen(
                    artikelID,
                    neuerBestand - aktuellerBestand
            );

            Ereignis ereignis = new Ereignis(
                    LocalDate.now().getDayOfYear(),
                    artikelVW.findeArtikel(artikelID),
                    neuerBestand - aktuellerBestand,
                    "Einlagerung",
                    "m:" + mitarbeiter
            );

            ereignisse.add(ereignis);

        } else {

            artikelVW.bestandVerringern(
                    artikelID,
                    aktuellerBestand - neuerBestand
            );

            Ereignis ereignis = new Ereignis(
                    LocalDate.now().getDayOfYear(),
                    artikelVW.findeArtikel(artikelID),
                    aktuellerBestand - neuerBestand,
                    "Auslagerung",
                    "m:" + mitarbeiter
            );

            ereignisse.add(ereignis);
        }

        speichereArtikel();
    }

    public boolean istMassengutartikel(int artikelID) {
        return artikelVW.istMassengutartikel(artikelID);
    }

    public int getPackungGroesse(int artikelID) {
        return artikelVW.getPackungGroesse(artikelID);
    }

    public void packungGroesseVeraendern(int artikelID, int neueGroesse) {
        artikelVW.packungGroesseVeraendern(artikelID, neueGroesse);
    }

    public int getBestand(int artikelID) {
        return artikelVW.getBestand(artikelID);
    }

    public String getArtikelName(int artikelID) {
        return artikelVW.getArtikelName(artikelID);
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

    public void pruefeArtikelExistiertBereits(String bezeichnung) {

        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID != -1) {

            Artikel artikel = artikelVW.findeArtikel(artikelID);

            throw new ArtikelExistiertBereitsException(artikel, "");
        }
    }
}
