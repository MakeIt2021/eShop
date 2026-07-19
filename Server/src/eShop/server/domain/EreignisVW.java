package eShop.server.domain;

import eShop.common.exceptions.DateiNichtGefundenException;
import eShop.common.entities.Artikel;
import eShop.common.entities.Ereignis;
import eShop.server.persistence.FilePersistenceManager;
import eShop.server.persistence.PersistenceManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

/**
 * Verwaltet die Ereignisse des eShops.
 *
 * Ein Ereignis beschreibt eine Bestandsänderung eines Artikels,
 * beispielsweise eine Einlagerung oder eine Auslagerung.
 *
 * Die Klasse übernimmt insbesondere:
 * <ul>
 *     <li>das Laden der Ereignisse aus einer Datei,</li>
 *     <li>das Speichern der Ereignisse in einer Datei,</li>
 *     <li>das Hinzufügen neuer Ereignisse,</li>
 *     <li>die Berechnung der Bestandshistorie eines Artikels.</li>
 * </ul>
 *
 * Die Abkürzung {@code VW} steht für Verwaltung.
 */
public class EreignisVW {
    /**
     * Persistenzmanager zum Lesen und Schreiben der Ereignisdaten.
     *
     * Als konkrete Implementierung wird ein
     * {@link FilePersistenceManager} verwendet.
     */
    private PersistenceManager pm = new FilePersistenceManager();

    /**
     * Liste aller geladenen und neu erzeugten Ereignisse.
     */
    ArrayList<Ereignis> ereignisListe = new ArrayList<>();

    /**
     * Lädt die gespeicherten Ereignisse aus einer Datei.
     *
     * Für jedes geladene Ereignis wird mithilfe der übergebenen
     * Suchfunktion versucht, den zugehörigen Artikel zu finden.
     *
     * Wird kein entsprechender Artikel gefunden, wird ein Ersatzartikel
     * mit der gespeicherten Artikel-ID, der gespeicherten Bezeichnung
     * und einem Preis von {@link BigDecimal#ZERO} erstellt.
     *
     * @param datei Pfad oder Name der zu lesenden Datei
     * @param findeArtikel Funktion zum Suchen eines Artikels anhand seiner ID
     * @throws DateiNichtGefundenException wenn die Datei nicht gelesen werden kann
     */
    public void ladeEreignisse(String datei, Function<String, Artikel> findeArtikel) throws DateiNichtGefundenException {
        try {
            // Datei zum Lesen öffnen.
            pm.openForReading(datei);

            // Gespeicherte Ereignisinformationen laden.
            ArrayList<PersistenceManager.einEreignisInfo> gespeichertEreignisListe = pm.ladeEreignisse();

            // Datei wieder schließen.
            pm.close();

            // Aus den geladenen Informationen Ereignisobjekte erzeugen.
            for (PersistenceManager.einEreignisInfo einEreignis : gespeichertEreignisListe) {
                Artikel artikel = findeArtikel.apply(String.valueOf(einEreignis.artikelID()));

                /*
                 * Falls der Artikel nicht mehr im Sortiment existiert,
                 * wird ein Ersatzartikel für die Historie erzeugt.
                 */
                if (artikel == null) {
                    artikel = new Artikel(einEreignis.artikelID(), einEreignis.bezeichnung(), BigDecimal.ZERO);
                }

                ereignisListe.add(new Ereignis(
                        einEreignis.date(),
                        artikel,
                        einEreignis.menge(),
                        einEreignis.typ(),
                        einEreignis.person()
                ));
            }
        } catch (IOException e) {
            /*
             * Die technische IOException wird in eine
             * projektspezifische Exception umgewandelt.
             */
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Speichert alle Ereignisse in einer Datei.
     *
     * @param datei Pfad oder Name der Zieldatei
     * @throws DateiNichtGefundenException wenn die Datei nicht geschrieben
     *                                     oder geöffnet werden kann
     */
    public void speichereEreignisse(String datei) throws DateiNichtGefundenException {
        try {
            // Datei zum Schreiben öffnen.
            pm.openForWriting(datei);

            // Gesamte Ereignisliste speichern.
            pm.speichereEreignis(ereignisListe);

            // Datei wieder schließen.
            pm.close();
        } catch (IOException e) {
            /*
             * Die technische IOException wird in eine
             * projektspezifische Exception umgewandelt.
             */
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Erstellt ein neues Ereignis und fügt es der Ereignisliste hinzu.
     *
     * Als Datum wird automatisch das aktuelle Datum verwendet.
     * Nach dem Hinzufügen wird die gesamte Ereignisliste in der Datei
     * {@code Ereignisse.txt} gespeichert.
     *
     * @param einArtikel Artikel, dessen Bestand verändert wurde
     * @param menge Menge der Bestandsänderung
     * @param typ Art des Ereignisses, beispielsweise
     *            {@code EINLAGERUNG} oder {@code AUSLAGERUNG}
     * @param person Name oder Kennung der verantwortlichen Person
     * @throws DateiNichtGefundenException wenn die Ereignisliste
     *                                     nicht gespeichert werden kann
     */
    public void addEreignis(Artikel einArtikel, int menge, String typ, String person) throws DateiNichtGefundenException {
        // Neues Ereignis mit dem aktuellen Datum erzeugen.
        ereignisListe.add(new Ereignis(
                LocalDate.now(),
                einArtikel,
                menge,
                typ,
                person
        ));

        /*
         * Die aktualisierte Ereignisliste speichern.
         *
         * TODO: Der Dateipfad sollte nicht direkt in dieser
         * Methode festgelegt werden.
         */
        speichereEreignisse("Server/resources/Ereignisse.txt"); // TODO: Nicht hier Filepath zu schreiben!
    }

    /**
     * Gibt die vollständige Ereignisliste zurück.
     *
     * @return Liste aller Ereignisse
     */
    public ArrayList<Ereignis> gibEreignisListe() {
        return ereignisListe;
    }

    /**
     * Berechnet die Bestandshistorie eines bestimmten Artikels.
     *
     * Zunächst werden alle Ereignisse ausgewählt, die zum angegebenen
     * Artikel gehören. Anschließend wird der Bestand chronologisch
     * berechnet:
     *
     * <ul>
     *     <li>Bei einer Einlagerung wird die Menge addiert.</li>
     *     <li>Bei einer Auslagerung wird die Menge subtrahiert.</li>
     * </ul>
     *
     * Von der vollständigen Historie werden höchstens die letzten
     * 30 Einträge zurückgegeben.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return Zuordnung eines Datums zum berechneten Artikelbestand
     */
    public Map<LocalDate, Integer> gibBestandHistorie(int artikelID) {
        // Anfangsbestand vor der Verarbeitung der Ereignisse.
        int bestand = 0;

        // Nur Ereignisse des gewünschten Artikels auswählen.
        List<Ereignis> events = ereignisListe.stream()
                .filter(e -> e.getArtikel() != null && e.getArtikel().getArtikelID() == artikelID)
                .toList();

        /*
         * LinkedHashMap bewahrt die Reihenfolge,
         * in der die Einträge eingefügt werden.
         */
        Map<LocalDate, Integer> vollstaendigeHistorie = new LinkedHashMap<>();

        // Bestand schrittweise anhand der Ereignisse berechnen.
        for (Ereignis e : events) {
            if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")) {
                bestand += e.getMenge();
            } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                bestand -= e.getMenge();
            }

            // Berechneten Bestand für das Ereignisdatum speichern.
            vollstaendigeHistorie.put(e.getTag(), bestand);
        }

        Map<LocalDate, Integer> historie = new LinkedHashMap<>();

        /*
         * Bestimmen, wie viele ältere Einträge übersprungen
         * werden müssen, damit höchstens 30 übrig bleiben.
         */
        int ignorieren = Math.max(0, vollstaendigeHistorie.size() - 30);

        // Nur die letzten 30 Historieneinträge übernehmen.
        vollstaendigeHistorie.entrySet().stream()
                .skip(ignorieren)
                .forEach(entry -> historie.put(entry.getKey(), entry.getValue()));

        return historie;
    }
}