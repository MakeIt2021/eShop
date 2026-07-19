package eShop.server.persistence;

import eShop.common.entities.Artikel;
import eShop.common.entities.Benutzer;
import eShop.common.entities.Ereignis;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;

/**
 * Definiert die Schnittstelle für den Zugriff auf die
 * dauerhafte Speicherung (Persistenz) des eShops.
 *
 * Implementierungen dieser Schnittstelle übernehmen das
 * Lesen und Schreiben von Benutzern, Artikeln,
 * Artikelbeständen und Ereignissen.
 *
 * Dadurch bleibt der Anwendungskern unabhängig von der
 * verwendeten Speichertechnik (z. B. Dateien oder Datenbank).
 */
public interface PersistenceManager {
    /**
     * Öffnet eine Datenquelle zum Lesen.
     *
     * Nach dem Öffnen können die verschiedenen Lademethoden
     * auf die Datenquelle zugreifen.
     *
     * @param datenquelle Name oder Pfad der Datenquelle
     * @throws IOException wenn die Datenquelle nicht geöffnet
     *                     werden kann
     */
    void openForReading(String datenquelle) throws IOException;

    /**
     * Öffnet eine Datenquelle zum Schreiben.
     *
     * Nach dem Öffnen können Speichermethoden Daten in die
     * Datenquelle schreiben.
     *
     * @param datenquelle Name oder Pfad der Datenquelle
     * @throws IOException wenn die Datenquelle nicht geöffnet
     *                     werden kann
     */
    void openForWriting(String datenquelle) throws IOException;

    /**
     * Schließt die aktuell geöffnete Datenquelle.
     *
     * Offene Dateien oder andere Ressourcen werden dadurch
     * ordnungsgemäß freigegeben.
     */
    void close();

    /**
     * Enthält alle Informationen eines gespeicherten Ereignisses.
     *
     * Das Record dient als einfache Datenstruktur zum
     * Transport der aus der Persistenz geladenen Informationen.
     *
     * @param date Datum des Ereignisses
     * @param artikelID ID des betroffenen Artikels
     * @param bezeichnung Bezeichnung des Artikels
     * @param menge betroffene Artikelmenge
     * @param typ Typ des Ereignisses
     * @param person Person, welche das Ereignis ausgelöst hat
     */
    record einEreignisInfo(
            LocalDate date,
            int artikelID,
            String bezeichnung,
            int menge,
            String typ,
            String person
    ) {}

    /**
     * Speichert alle Benutzer dauerhaft.
     *
     * Der Schlüssel der Map entspricht der Benutzerkennung.
     *
     * @param benutzer Map mit allen Benutzern
     * @throws IOException wenn die Daten nicht gespeichert
     *                     werden können
     */
    void speicherBenutzer(HashMap<String, Benutzer> benutzer) throws IOException;

    /**
     * Lädt alle gespeicherten Benutzer.
     *
     * @return Map mit allen Benutzern
     * @throws IOException wenn die Daten nicht gelesen
     *                     werden können
     */
    HashMap<String, Benutzer> ladeBenutzer() throws IOException;

    /**
     * Lädt den nächsten gespeicherten Artikel.
     *
     * Wiederholtes Aufrufen der Methode liefert nacheinander
     * alle gespeicherten Artikel.
     *
     * @return geladener Artikel oder {@code null},
     *         wenn keine weiteren Artikel vorhanden sind
     * @throws IOException wenn die Daten nicht gelesen
     *                     werden können
     */
    Artikel ladeArtikel() throws IOException;

    /**
     * Lädt alle gespeicherten Artikelbestände.
     *
     * Der Schlüssel der Map entspricht der Artikel-ID,
     * der Wert dem aktuellen Bestand.
     *
     * @return Map mit allen Artikelbeständen
     * @throws IOException wenn die Daten nicht gelesen
     *                     werden können
     */
    HashMap<Integer, Integer> ladeArtikelMenge() throws IOException;

    /**
     * Speichert einen einzelnen Artikel.
     *
     * @param a zu speichernder Artikel
     * @throws IOException wenn der Artikel nicht gespeichert
     *                     werden kann
     */
    void speichereArtikel(Artikel a) throws IOException;

    /**
     * Speichert alle Artikelbestände.
     *
     * @param artikelMengeListe Map mit allen Artikelbeständen
     * @throws IOException wenn die Daten nicht gespeichert
     *                     werden können
     */
    void speichereArtikelMenge(HashMap<Integer, Integer> artikelMengeListe) throws IOException;

    /**
     * Lädt alle gespeicherten Ereignisse.
     *
     * @return Liste mit allen Ereignisinformationen
     * @throws IOException wenn die Daten nicht gelesen
     *                     werden können
     */
    ArrayList<einEreignisInfo> ladeEreignisse() throws IOException;

    /**
     * Speichert alle Ereignisse dauerhaft.
     *
     * @param ereignisse Liste der zu speichernden Ereignisse
     * @throws IOException wenn die Daten nicht gespeichert
     *                     werden können
     */
    void speichereEreignis(ArrayList<Ereignis> ereignisse) throws IOException;
}