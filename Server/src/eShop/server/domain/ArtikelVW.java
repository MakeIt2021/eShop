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

/**
 * Verwaltet alle Artikel und deren Bestände im eShop.
 *
 * Die Klasse übernimmt das Laden und Speichern von Artikeln,
 * die Verwaltung der verfügbaren Artikelmengen sowie die Suche
 * und Änderung von Artikeldaten.
 *
 * Artikel und ihre Bestände werden in zwei getrennten Maps verwaltet:
 * Eine Map enthält die eigentlichen Artikelobjekte, während die andere
 * Map die zugehörigen Bestandsmengen speichert.
 *
 * Die Abkürzung {@code VW} steht für Verwaltung.
 */
public class ArtikelVW {
    /**
     * Enthält alle Artikel des eShops.
     *
     * Der Schlüssel entspricht der eindeutigen Artikel-ID.
     * Der Wert ist das zugehörige Artikelobjekt.
     */
    private final HashMap<Integer, Artikel> artikelListe = new HashMap<>();
    /**
     * Enthält den aktuellen Bestand jedes Artikels.
     *
     * Der Schlüssel entspricht der Artikel-ID.
     * Der Wert entspricht der verfügbaren Menge des Artikels.
     */
    private HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();
    /**
     * Persistenzmanager zum Laden und Speichern der Artikel-
     * und Bestandsdaten.
     */
    private final PersistenceManager pm = new FilePersistenceManager();

    /**
     * Nächste freie Artikel-ID.
     * Der Wert 1010 wurde gewählt, da einige Testartikel zur Erleichterung der Tests bereits hinzugefügt wurden.
     */
    private int nextId = 1010;

    /**
     * Lädt die gespeicherten Artikelmengen und anschließend
     * die zugehörigen Artikeldaten.
     *
     * Aus dem übergebenen Basisnamen werden automatisch zwei
     * Dateinamen erstellt:
     * <ul>
     *     <li>{@code datei + "_AM.txt"} für die Artikelmengen</li>
     *     <li>{@code datei + "_A.txt"} für die Artikeldaten</li>
     * </ul>
     *
     * @param datei Basisname der zu ladenden Dateien
     * @throws DateiNichtGefundenException wenn eine Datei nicht
     *                                     geöffnet oder gelesen werden kann
     */
    public void ladeArtikelMengeDaten (String datei) throws DateiNichtGefundenException {
        String dateiAM = datei+"_AM.txt";
        try {
            // Datei mit den Artikelmengen zum Lesen öffnen.
            pm.openForReading(dateiAM);

            // Gespeicherte Artikelmengen laden.
            artikelMengeListe = pm.ladeArtikelMenge();

            // Anschließend die zugehörigen Artikeldaten laden.
            ladeArtikelDaten(datei + "_A.txt");
        } catch (IOException e) {
            /*
             * Die IOException wird in eine projektspezifische
             * Exception umgewandelt.
             */
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Lädt alle Artikel aus einer Datei.
     *
     * Die Artikel werden nacheinander gelesen und anhand ihrer
     * Artikel-ID in der Artikelliste gespeichert.
     *
     * @param datei Name oder Pfad der zu lesenden Datei
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     geöffnet oder gelesen werden kann
     */
    public void ladeArtikelDaten(String datei) throws DateiNichtGefundenException {
        try {
            // Datei zum Lesen öffnen.
            pm.openForReading(datei);

            Artikel einArtikel;

            /*
             * Artikel so lange laden, bis der Persistenzmanager
             * keinen weiteren Artikel mehr zurückgibt.
             */
            while ((einArtikel = pm.ladeArtikel()) != null) {
                artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            }

            // Datei nach dem Lesen schließen.
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Speichert alle Bestandsmengen in einer Datei.
     *
     * Die Bestandsdaten werden nur geschrieben, wenn die
     * Bestandsliste mindestens einen Eintrag enthält.
     *
     * @param datei Name oder Pfad der Zieldatei
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     geöffnet oder geschrieben werden kann
     */
    public void speichereArtikelMengeDaten(String datei) throws DateiNichtGefundenException {
        try {
            // Datei zum Schreiben öffnen.
            pm.openForWriting(datei);

            // Bestände nur speichern, wenn Daten vorhanden sind.
            if (!artikelMengeListe.isEmpty())
                pm.speichereArtikelMenge(artikelMengeListe);

            // Datei nach dem Schreiben schließen.
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Speichert alle Artikel in einer Datei.
     *
     * Jeder Artikel der Artikelliste wird einzeln an den
     * Persistenzmanager übergeben.
     *
     * @param datei Name oder Pfad der Zieldatei
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     geöffnet oder geschrieben werden kann
     */
    public void speichereArtikelDaten(String datei) throws DateiNichtGefundenException {
        // PersistenzManager für Schreibvorgänge öffnen
        try {
            pm.openForWriting(datei);

            /*
             * Alle gespeicherten Artikel durchlaufen
             * und einzeln in die Datei schreiben.
             */
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

    /**
     * Erhöht den Bestand eines Artikels um eine bestimmte Menge.
     *
     * Ist für die angegebene Artikel-ID noch kein Bestand gespeichert,
     * wird als bisheriger Bestand der Wert {@code 0} verwendet.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge Menge, um die der Bestand erhöht werden soll
     */
    public void bestandErhoehen(int artikelID, int menge) {
        // Aktuellen Bestand lesen oder bei fehlendem Eintrag 0 verwenden.
        int current = artikelMengeListe.getOrDefault(artikelID,0);

        // Neue Bestandsmenge speichern.
        artikelMengeListe.put(artikelID, current + menge);
    }

    /**
     * Verringert den Bestand eines Artikels um eine bestimmte Menge.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge Menge, um die der Bestand verringert werden soll
     */
    public void bestandVerringern(int artikelID, int menge) {
        artikelMengeListe.put(artikelID, artikelMengeListe.get(artikelID) - menge);
    }

    /**
     * Fügt einen Artikel mit einer bestimmten Anfangsmenge ein.
     *
     * Ist der übergebene Artikel {@code null}, wird die Methode
     * ohne Änderung beendet.
     *
     * Existiert bereits ein Artikel mit derselben ID, wird lediglich
     * dessen Bestand erhöht. Andernfalls werden der neue Artikel
     * und seine Anfangsmenge in die entsprechenden Maps eingefügt.
     *
     * Eine negative Anfangsmenge wird beim Einfügen eines neuen
     * Artikels durch {@code 0} ersetzt.
     *
     * @param einArtikel einzufügender Artikel
     * @param menge Anfangsmenge oder zusätzlich einzulagernde Menge
     */
    public void einfuegen(Artikel einArtikel, int menge) {
        // Nullwerte werden nicht verarbeitet
        if (einArtikel == null) return;

        /*
         * Existiert der Artikel bereits, wird nur der
         * bestehende Bestand erhöht. Es war aktuell in CUI.
         */
        if (gibArtikelListe().containsKey(einArtikel.getArtikelID()))
            bestandErhoehen(einArtikel.getArtikelID(), menge);
        else {
            // Neuen Artikel einfügen
            artikelListe.put(einArtikel.getArtikelID(), einArtikel);
            /*
             * Negative Anfangsmengen verhindern und
             * mindestens den Bestand 0 speichern.
             */
            artikelMengeListe.put(einArtikel.getArtikelID(), Math.max(menge, 0));
           }
        }

    /**
     * Ändert die Bezeichnung eines Artikels.
     *
     * Die neue Bezeichnung wird vor dem Speichern vollständig
     * in Kleinbuchstaben umgewandelt.
     *
     * @param artikelID eindeutige ID des zu ändernden Artikels
     * @param bezeichnung neue Artikelbezeichnung
     */
    public void bezeichnungVeraendern(int artikelID, String bezeichnung) {
        artikelListe.get(artikelID).setBezeichnung(bezeichnung.toLowerCase());
    }

    /**
     * Ändert den Preis eines Artikels.
     *
     * @param artikelID eindeutige ID des zu ändernden Artikels
     * @param preis neuer Preis des Artikels
     */
    public void preisVeraendern(int artikelID, BigDecimal preis) {
        artikelListe.get(artikelID).setPreis(preis);
    }

    /**
     * Entfernt einen Artikel vollständig aus der Verwaltung.
     *
     * Sowohl das Artikelobjekt als auch dessen gespeicherter
     * Bestand werden entfernt.
     *
     * @param artikelID eindeutige ID des zu entfernenden Artikels
     */
    public void artikelVernichten(int artikelID) {
        // Artikel aus der Artikelliste entfernen.
        artikelListe.remove(artikelID);

        // Zugehörigen Bestand entfernen.
        artikelMengeListe.remove(artikelID);
    }

    /**
     * Sucht einen Artikel anhand seiner ID.
     *
     * @param artikelID eindeutige ID des gesuchten Artikels
     * @return gefundener Artikel oder {@code null}, wenn kein
     *         entsprechender Artikel existiert
     */
    public Artikel findeArtikel(int artikelID) {
        return artikelListe.get(artikelID);
    }

    /**
     * Sucht die ID eines Artikels anhand seiner Bezeichnung.
     *
     * Die Groß- und Kleinschreibung der Bezeichnung wird
     * bei der Suche nicht berücksichtigt.
     *
     * @param bezeichnung Bezeichnung des gesuchten Artikels
     * @return ID des gefundenen Artikels oder {@code -1},
     *         wenn kein passender Artikel gefunden wurde
     */
    public int sucheNachIDMitBezeichnung(String bezeichnung) {
        // Alle Artikel der Artikelliste durchsuchen.
        for (Artikel a : artikelListe.values()) {
            if (Objects.equals(a.getBezeichnung().toLowerCase(), bezeichnung.toLowerCase()))
                return a.getArtikelID();
        }

        // Kein Artikel mit der angegebenen Bezeichnung gefunden.
        return -1;
    }

    /**
     * Gibt die vollständige Artikelliste zurück.
     *
     * @return Zuordnung der Artikel-IDs zu den Artikelobjekten
     */
    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelListe;
    }

    /**
     * Gibt die vollständige Bestandsliste zurück.
     *
     * @return Zuordnung der Artikel-IDs zu den Bestandsmengen
     */
    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelMengeListe;
    }

    /**
     * Gibt den aktuellen Bestand eines Artikels zurück.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return aktuell gespeicherte Bestandsmenge
     */
    public int getBestand(int artikelID) {
        return artikelMengeListe.get(artikelID);
    }

    /**
     * Prüft, ob ein Artikel ein Massengutartikel ist.
     *
     * Dafür wird geprüft, ob das gespeicherte Artikelobjekt
     * eine Instanz von {@link Massengutartikel} ist.
     *
     * @param artikelID eindeutige ID des zu prüfenden Artikels
     * @return {@code true}, wenn es sich um einen Massengutartikel
     *         handelt, sonst {@code false}
     */
    public boolean istMassengutartikel(int artikelID) {
        return artikelListe.get(artikelID) instanceof Massengutartikel;
    }

    /**
     * Gibt die Packungsgröße eines Massengutartikels zurück.
     *
     * Der gespeicherte Artikel wird dafür in einen
     * {@link Massengutartikel} umgewandelt.
     *
     * @param artikelID eindeutige ID des Massengutartikels
     * @return Packungsgröße des Artikels
     */
    public int getPackungGroesse(int artikelID) {
        return ((Massengutartikel) artikelListe.get(artikelID)).getPackungGroesse();
    }

    /**
     * Ändert die Packungsgröße eines Massengutartikels.
     *
     * Der gespeicherte Artikel wird dafür in einen
     * {@link Massengutartikel} umgewandelt.
     *
     * @param artikelID eindeutige ID des Massengutartikels
     * @param neueGroesse neue Packungsgröße
     */
    public void packungGroesseVeraendern(int artikelID, int neueGroesse) {
        ((Massengutartikel) artikelListe.get(artikelID)).setPackungGroesse(neueGroesse);
    }

    /**
     * Gibt die Bezeichnung eines Artikels zurück.
     *
     * Die zurückgegebene Bezeichnung wird vollständig
     * in Kleinbuchstaben umgewandelt.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return Bezeichnung des Artikels in Kleinbuchstaben
     */
    public String getArtikelName(int artikelID) {
        return artikelListe.get(artikelID).getBezeichnung().toLowerCase();
    }

    /**
     * Gibt den Preis eines Artikels zurück.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return Preis des Artikels
     */
    public BigDecimal gibPreis(int artikelID) {
        return artikelListe.get(artikelID).getPreis();
    }

    public int generiereArtikelID() {
        return nextId++;
    }
}
