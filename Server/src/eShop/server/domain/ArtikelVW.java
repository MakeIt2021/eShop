package eShop.server.domain;

import eShop.common.exceptions.DateiNichtGefundenException;
import eShop.common.entities.Artikel;
import eShop.common.entities.Massengutartikel;
import eShop.server.persistence.FilePersistenceManager;
import eShop.server.persistence.PersistenceManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArtikelVW {
    private final HashMap<Integer, Artikel> artikelListe = new HashMap<>();
    private HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();
    private final PersistenceManager pm = new FilePersistenceManager();

    public void ladeArtikelMengeDaten (String datei) throws DateiNichtGefundenException {
        String dateiAM = datei+"_AM.txt";
        try {
            pm.openForReading(dateiAM);
            artikelMengeListe = pm.ladeArtikelMenge();
            ladeArtikelDaten(datei + "_A.txt");
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

   public void ladeArtikelDaten(String datei) throws DateiNichtGefundenException {
        try {
            pm.openForReading(datei);

            Artikel einArtikel;
            while ((einArtikel = pm.ladeArtikel()) != null) {
                artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            }

            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
   }

    public void speichereArtikelMengeDaten(String datei) throws DateiNichtGefundenException {
        try {
            pm.openForWriting(datei);

            if (!artikelMengeListe.isEmpty())
                pm.speichereArtikelMenge(artikelMengeListe);

            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    public void speichereArtikelDaten(String datei) throws DateiNichtGefundenException {
        // PersistenzManager für Schreibvorgänge öffnen
        try {
            pm.openForWriting(datei);

            // Durchlaufen einer Liste mit einem Iterator:
            if (!artikelListe.isEmpty()) {
                for (Map.Entry<Integer, Artikel> entry : artikelListe.entrySet()) {
                    pm.speichereArtikel(entry.getValue());
                }
            }

            // Persistenz-Schnittstelle wieder schließen
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    public void bestandErhoehen(int artikelID, int menge) {
        int current = artikelMengeListe.getOrDefault(artikelID,0);
        artikelMengeListe.put(artikelID, current + menge);
    }

    public void bestandVerringern(int artikelID, int menge) {
        artikelMengeListe.put(artikelID, artikelMengeListe.get(artikelID) - menge);
    }

    public void einfuegen(Artikel einArtikel, int menge) {
        if (einArtikel == null) return;
        if (gibArtikelListe().containsKey(einArtikel.getArtikelID()))
           bestandErhoehen(einArtikel.getArtikelID(), menge);
       else {
            artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            artikelMengeListe.put(einArtikel.getArtikelID(), Math.max(menge, 0));
           }
        }

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) {
        artikelListe.get(artikelID).setBezeichnung(bezeichnung.toLowerCase());
    }

    public void preisVeraendern(int artikelID, BigDecimal preis) {
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
            if (Objects.equals(a.getBezeichnung().toLowerCase(), bezeichnung.toLowerCase()))
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
        return artikelListe.get(artikelID).getBezeichnung().toLowerCase();
    }

    public BigDecimal gibPreis(int artikelID) {
        return artikelListe.get(artikelID).getPreis();
    }
}
