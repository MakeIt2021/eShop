package domain;

import domain.exceptions.ArtikelExistiertBereitsException;
import entities.Artikel;
import entities.Massengutartikel;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArtikelVW {
    private HashMap<Integer, Artikel> artikelListe = new HashMap<>();
    private HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();
    private PersistenceManager pm = new FilePersistenceManager();

    public void ladeArtikelMengeDaten (String datei) throws IOException {
        String dateiAM = datei+"_AM.txt";
        pm.openForReading(dateiAM);
        artikelMengeListe = pm.ladeArtikelMenge();
        ladeArtikelDaten(datei+"_A.txt");
    }

    public void ladeArtikelDaten(String datei) throws IOException {
        pm.openForReading(datei);

        Artikel einArtikel;

        do {
            einArtikel = pm.ladeArtikel();

            if (einArtikel != null) {
                try {
                    einfuegen(einArtikel, artikelMengeListe.get(einArtikel.getArtikelID()));
                } catch (ArtikelExistiertBereitsException e1) {}
            }
        } while (einArtikel != null);

        pm.close();
    }

    public void speichereArtikelMengeDaten(String datei) throws IOException {
        pm.openForWriting(datei);

        if (!artikelMengeListe.isEmpty())
            pm.speichereArtikelMenge(artikelMengeListe);

        pm.close();
    }

    public void speichereArtikelDaten(String datei) throws IOException  {
        // PersistenzManager für Schreibvorgänge öffnen
        pm.openForWriting(datei);

        // Durchlaufen einer Liste mit einem Iterator:
        if (!artikelListe.isEmpty()) {
            for (Map.Entry<Integer, Artikel> entry : artikelListe.entrySet()) {
                pm.speichereArtikel(entry.getValue());
            }
        }

        // Persistenz-Schnittstelle wieder schließen
        pm.close();
    }

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

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) {
        artikelListe.get(artikelID).setBezeichnung(bezeichnung);
    }

    public void preisVeraendern(int artikelID, float preis) {
        artikelListe.get(artikelID).setPreis(preis);
    }

    public void artikelVernichten(int artikelID) {
        artikelListe.remove(artikelID);
        artikelMengeListe.remove(artikelID);
    }

    public Artikel findeArtikel(int artikelID) {
        return artikelListe.get(artikelID);
    }

    public int sucheNachIDMitBezeichnung(String bezeichnung) {
        for (Artikel a : artikelListe.values()) {
            if (Objects.equals(a.getBezeichnung(), bezeichnung))
                return a.getArtikelID();
        }

        return -1;
    }

    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelListe;
    }
    
    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelMengeListe;
    }

    public int getBestand(int artikelID) {
        return artikelMengeListe.get(artikelID);
    }

    public boolean istMassengutartikel(int artikelID) {
        return artikelListe.get(artikelID) instanceof Massengutartikel;
    }

    public int getPackungGroesse(int artikelID) {
        return ((Massengutartikel) artikelListe.get(artikelID)).getPackungGroesse();
    }

    public void packungGroesseVeraendern(int artikelID, int neueGroesse) {
        ((Massengutartikel) artikelListe.get(artikelID)).setPackungGroesse(neueGroesse);
    }

    public String getArtikelName(int artikelID) {
        return artikelListe.get(artikelID).getBezeichnung();
    }
}
