package persistence;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;

import java.util.ArrayList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface PersistenceManager {
    void openForReading(String datenquelle) throws IOException;
    void openForWriting(String datenquelle) throws IOException;
    void close();

//speichern den benutzer
    void speicherBenutzer(Benutzer benutzer) throws IOException;
    Map<String, Benutzer> ladeBenutzer() throws IOException;

    /**
     * Methode zum Einlesen der Artikeldaten aus einer externen Datenquelle.
     *
     * @return Artikel-Objekt, wenn Einlesen erfolgreich, false null
     */
    Artikel ladeArtikel() throws IOException;
    HashMap<Integer, Integer> ladeArtikelMenge() throws IOException;

    /**
     * Methode zum Schreiben der Artikeldaten in eine externe Datenquelle.
     *
     * @param a Artikel-Objekt, das gespeichert werden soll
     */
    void speichereArtikel(Artikel a) throws IOException;
    void speichereArtikelMenge(HashMap<Integer, Integer> artikelMengeListe) throws IOException;


  // public Warenkorb ladeWarenkorb() throws IOException;
//  public boolean speichereWarenkorb(Warenkorb w) throws IOException;
	/*
	 *  Für Kunden und Ereignisse!
	*/

    void speichereEreignis(ArrayList<Ereignis> ereignisse) throws IOException;

    ArrayList<Ereignis> ladeEreignisse();
}
