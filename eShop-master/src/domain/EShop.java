package domain;

import java.util.List;
import java.util.Comparator;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class EShop {
    private String datei = "eShop";

    public ArtikelVW getArtikelVW() {
        return artikelVW;
    }

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
        ArrayList<Ereignis> historieVon30Tagen = pm.ladeEreignisse();
        ereignisse = new ArrayList<>();

        for (Ereignis e : historieVon30Tagen) {
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
        int bestand = 0;

        List<Ereignis> events = ereignisse.stream().filter(e -> e.getArtikel().getArtikelID() == artikelID).toList();

        Map<Integer, Integer> historie = new HashMap<>();

        int counter = 0;
        for (Ereignis e : events) {
            if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")) {
                bestand += e.getMenge();
            } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                bestand -= e.getMenge();
            }
            if (counter == 30) {
                break;
            }
            historie.put(e.getTag(), bestand);
            counter += 1;
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
            throw new UngueltigerPreisException(preis);
        }
        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        ereignisse.add(new Ereignis(
                LocalDate.now().getDayOfYear(),
                art,
                menge,
                "Einlagerung",
                "m:" + mitarbeiter
        ));

        pm.speichereEreignis(ereignisse);
        speichereArtikel();
    }

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

        speichereArtikel();
    }

    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws IOException {

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

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
        pm.speichereEreignis(ereignisse);
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

    public void pruefeArtikelExistiertBereits(String bezeichnung) {

        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID != -1) {

            Artikel artikel = artikelVW.findeArtikel(artikelID);

            throw new ArtikelExistiertBereitsException(artikel, "");
        }
    }
}
