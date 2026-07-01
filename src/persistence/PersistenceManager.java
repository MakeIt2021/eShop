package persistence;

import entities.Artikel;
import entities.Benutzer;
import entities.Ereignis;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public interface PersistenceManager {
    void openForReading(String datenquelle) throws IOException;
    void openForWriting(String datenquelle) throws IOException;
    void close();
    record einEreignisInfo(
            LocalDate date,
            int artikelID,
            String bezeichnung,
            int menge,
            String typ,
            String person
    ) {}

    //speichern den benutzer
    void speicherBenutzer(HashMap<String, Benutzer> benutzer) throws IOException;
    HashMap<String, Benutzer> ladeBenutzer() throws IOException;

    Artikel ladeArtikel() throws IOException;
    HashMap<Integer, Integer> ladeArtikelMenge() throws IOException;

    void speichereArtikel(Artikel a) throws IOException;
    void speichereArtikelMenge(HashMap<Integer, Integer> artikelMengeListe) throws IOException;

    ArrayList<einEreignisInfo> ladeEreignisse() throws IOException;
    void speichereEreignis(ArrayList<Ereignis> ereignisse) throws IOException;
}