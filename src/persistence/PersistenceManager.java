package persistence;

import domain.WarenkorbVW;
import entities.Artikel;
import entities.Warenkorb;
import entities.Benutzer;
import entities.Mitarbeiter;
import entities.Kunde;
import java.io.*;
import java.util.*;
import entities.Ereignis;

import java.util.ArrayList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface PersistenceManager {
    public void openForReading(String datenquelle) throws IOException;
    public void openForWriting(String datenquelle) throws IOException;
    public boolean close();

//speichern den benutzer
    void speicherBenutzer(Benutzer benutzer) throws IOException;
    Map<String, Benutzer> ladeBenutzer() throws IOException;

    /**
     * Methode zum Einlesen der Artikeldaten aus einer externen Datenquelle.
     *
     * @return Artikel-Objekt, wenn Einlesen erfolgreich, false null
     */
    public Artikel ladeArtikel() throws IOException;
    public HashMap<Integer, Integer> ladeArtikelMenge() throws IOException;

    /**
     * Methode zum Schreiben der Artikeldaten in eine externe Datenquelle.
     *
     * @param a Artikel-Objekt, das gespeichert werden soll
     * @return true, wenn Schreibvorgang erfolgreich, false sonst
     */
    public boolean speichereArtikel(Artikel a) throws IOException;
    public boolean speichereArtikelMenge(HashMap<Integer, Integer> b) throws IOException;


//    public Warenkorb ladeWarenkorb() throws IOException;
//    public boolean speichereWarenkorb(Warenkorb w) throws IOException;
	/*
	 *  Für Kunden und Ereignisse!
	*/

    public void speichereEreignisArtikel(ArrayList<Ereignis> ereignisse) throws IOException;
}
