package domain;

import entities.Artikel;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//import bib.local.domain.exceptions.BuchExistiertBereitsException;
//import bib.local.persistence.FilePersistenceManager;
//import bib.local.persistence.PersistenceManager;
//import bib.local.entities.Buch;
//import bib.local.entities.BuchListe;


/**
 * Klasse zur Verwaltung von Büchern.
 *
 * @author teschke
 * @version 1 (Verwaltung in verketteter Liste)
 */
public class ArtikelVW {
    private HashMap<Integer, Artikel> artikelListe = new HashMap<>();


    public void einfuegen(Artikel einArtikel) {
        if (artikelListe.containsKey(einArtikel.getArtikelID())) {
            einArtikel.bestandErhoehen();
        } else {
            artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            int bestand = artikelListe.get(einArtikel.getArtikelID()).getBestand();
            System.out.println(bestand);
        }
    }

    public void loeschen(int artikelID) {
        artikelListe.get(artikelID).bestandVerringern();
    }

    public HashMap<Integer, Artikel> getArtikelBestand() {
        return new HashMap<>(artikelListe);
    }
}
