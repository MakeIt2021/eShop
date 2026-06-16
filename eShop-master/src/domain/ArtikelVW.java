package domain;

import domain.exceptions.ArtikelExistiertBereitsException;
import domain.exceptions.BestandNichtAusreichendException;
import entities.Artikel;
import entities.Ereignis;
import entities.Massengutartikel;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;
import java.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArtikelVW {
    private HashMap<Integer, Artikel> artikelListe = new HashMap<>();
    private HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();
    private PersistenceManager pm = new FilePersistenceManager();


    //bestandHistorie
    public void ladeEreignisse() {
        ArrayList<Ereignis> geladene = pm.ladeEreignisse();
        ereignisse.clear();
        ereignisse.addAll(geladene);
    }

    private final ArrayList<Ereignis> ereignisse = new ArrayList<>();

    public void addEreignis(Ereignis e) {
        ereignisse.add(e);
    }
    public int berechneBestand(int artikelID){
        int bestand =0;

        for(Ereignis e: ereignisse){
            if (e.getArtikel().getArtikelID() == artikelID){
                if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")){
                    bestand += e.getMenge();
                } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                  bestand -= e.getMenge();
                }
            }
        }
        return bestand;
    }

    public void ladeArtikelMengeDaten (String datei) throws IOException {
        String dateiAM = datei+"_AM.txt";
        pm.openForReading(dateiAM);
        artikelMengeListe = pm.ladeArtikelMenge();
        ladeArtikelDaten(datei+"_A.txt");
    }

   public void ladeArtikelDaten(String datei) throws IOException {
       pm.openForReading(datei);

       Artikel einArtikel;
       while ((einArtikel = pm.ladeArtikel()) != null) {
           int menge = artikelMengeListe.getOrDefault(einArtikel.getArtikelID(), 0);
           artikelListe.put(einArtikel.getArtikelID(), einArtikel);
       }

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
    //speicherArtikelEreigniss method
    public void speichereEreignisse() throws IOException {
        pm.speichereEreignis(ereignisse);
    }

    public void bestandErhoehen(int artikelID, int menge) {

        int current = artikelMengeListe.getOrDefault(artikelID,0);//check
       artikelMengeListe.put(artikelID, current + menge);//check
        Artikel artikel = findeArtikel(artikelID);
        if (artikel == null || menge <= 0 )
            return;
        addEreignis(new Ereignis(
                LocalDate.now().getDayOfYear(),
                artikel,
                menge, "EINLAGERUNG", "system"
        ));
    }

    public void bestandVerringern(int artikelID, int menge) {
        int bestand = artikelMengeListe.get(artikelID);

        if (bestand < menge) {
            throw new BestandNichtAusreichendException(artikelListe.get(artikelID).getBezeichnung(), bestand, menge);
        }

        artikelMengeListe.put(artikelID, artikelMengeListe.get(artikelID) - menge);
    }

    public void einfuegen(Artikel einArtikel, int menge) {
        if (einArtikel == null) return;
        if (gibArtikelListe().containsKey(einArtikel.getArtikelID()))
           bestandErhoehen(einArtikel.getArtikelID(), menge);
       else {
            artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            artikelMengeListe.put(einArtikel.getArtikelID(), Math.max(menge, 0));
            addEreignis(
                    new Ereignis(LocalDate.now().getDayOfYear(),
                            einArtikel,
                            menge,
                            "EINLAGERUNG",
                            "system"
                    )
                );
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
    //hier habe ich einbischen veranderungen gemacht
    public int gibBestand(int artikelID) {
        return artikelMengeListe.getOrDefault(artikelID, 0);
        /*Artikel a = findeArtikel(artikelID);
        if (a == null) return 0;
        return berechneBestand(artikelID);*/

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
