package eShop.server.persistence;

import eShop.common.entities.*;
import eShop.common.exceptions.DateiNichtGefundenException;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementiert die Dateipersistenz des eShops.
 *
 * Diese Klasse ist für das Lesen und Schreiben der dauerhaft
 * gespeicherten Daten zuständig. Dazu gehören:
 *
 * <ul>
 *     <li>Benutzerdaten,</li>
 *     <li>Artikeldaten,</li>
 *     <li>Artikelbestände,</li>
 *     <li>Ereignisse.</li>
 * </ul>
 *
 * Die Daten werden in Textdateien gespeichert. Dafür verwendet
 * die Klasse einen {@link BufferedReader} zum Lesen und einen
 * {@link PrintWriter} zum Schreiben.
 *
 * Die Klasse implementiert die durch {@link PersistenceManager}
 * vorgegebenen Persistenzoperationen.
 */
public class FilePersistenceManager implements PersistenceManager {
    /**
     * Reader zum zeilenweisen Lesen aus einer geöffneten Datei.
     *
     * Der Reader ist {@code null}, solange keine Datei zum Lesen
     * geöffnet wurde.
     */
    private BufferedReader reader = null;

    /**
     * Writer zum zeilenweisen Schreiben in eine geöffnete Datei.
     *
     * Der Writer ist {@code null}, solange keine Datei zum Schreiben
     * geöffnet wurde.
     */
    private PrintWriter writer = null;

    /**
     * Lädt alle Benutzer aus der Datei {@code benutzer.txt}.
     *
     * Jede Zeile enthält die Benutzerdaten, getrennt durch Semikolons.
     * Erwartet wird folgendes Format:
     *
     * <pre>
     * Benutzer-ID;Benutzerkennung;Vor- und Nachname;Passwort;Rolle
     * </pre>
     *
     * Anhand der gespeicherten Rolle wird entweder ein
     * {@link Kunde} oder ein {@link Mitarbeiter} erzeugt.
     *
     * Die Benutzer werden in einer Map gespeichert. Als Schlüssel
     * wird die Benutzerkennung verwendet.
     *
     * @return Map mit allen geladenen Benutzern
     * @throws DateiNichtGefundenException wenn die Benutzerdatei
     *                                     nicht gefunden oder gelesen
     *                                     werden kann
     */
    @Override
    public HashMap<String, Benutzer> ladeBenutzer() throws DateiNichtGefundenException{
        // Leere Map für die geladenen Benutzer erzeugen.
        HashMap<String, Benutzer> map = new HashMap<>();

        /*
         * Benutzerdatei öffnen.
         *
         * Hinweis: Der Dateiname ist im ursprünglichen Code
         * fest eingetragen.
         */
        openForReading("Server/resources/benutzer.txt"); //TODO: sollte nicht hardcoded sein

        try {
            String zeile;

            // Datei zeilenweise bis zum Dateiende lesen.
            while ((zeile = reader.readLine()) != null) {
                /*
                 * Die einzelnen Benutzerdaten sind durch
                 * Semikolons voneinander getrennt.
                 */
                String[] d = zeile.split(";");

                // Gespeicherte Werte aus der Zeile auslesen.
                int id = Integer.parseInt(d[0]);
                String erkennung = d[1].trim();
                String name = d[2].trim();
                String password = d[3].trim();
                String rolle = d[4].trim();

                Benutzer b;

                /*
                 * Abhängig von der Rolle wird das passende
                 * Benutzerobjekt erstellt.
                 */
                if (rolle.equalsIgnoreCase("kunde")) {
                    b = new Kunde(id, erkennung, name, password);
                }else {
                    b = new Mitarbeiter(id, erkennung, name, password);
                }

                /*
                 * Benutzer anhand seiner Benutzerkennung
                 * in der Map speichern.
                 */
                map.put(erkennung, b);
            }
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }

        return map;
    }

    /**
     * Öffnet eine Datei zum Lesen.
     *
     * Der erzeugte {@link BufferedReader} wird im Attribut
     * {@code reader} gespeichert und anschließend von den
     * Lademethoden verwendet.
     *
     * @param datei Name oder Pfad der zu öffnenden Datei
     * @throws DateiNichtGefundenException wenn die Datei
     *                                     nicht gefunden werden kann
     */
    public void openForReading(String datei) throws DateiNichtGefundenException {
        try {
            reader = new BufferedReader(new FileReader(datei));
        } catch (FileNotFoundException e) {
            throw new DateiNichtGefundenException(datei + "konnte nicht gefunden werden.");
        }
    }

    /**
     * Öffnet eine Datei zum Schreiben.
     *
     * Der erzeugte {@link PrintWriter} wird im Attribut
     * {@code writer} gespeichert und anschließend von den
     * Speichermethoden verwendet.
     *
     * Wird eine bereits vorhandene Datei geöffnet, wird ihr
     * bisheriger Inhalt überschrieben.
     *
     * @param datei Name oder Pfad der zu öffnenden Datei
     * @throws DateiNichtGefundenException wenn die Datei
     *                                     nicht zum Schreiben
     *                                     geöffnet werden kann
     */
    public void openForWriting(String datei) throws DateiNichtGefundenException {
        try {
            // Writer für die angegebene Datei erzeugen.
            writer = new PrintWriter(new BufferedWriter(new FileWriter(datei)));
        } catch (IOException e) {
            throw new DateiNichtGefundenException("Datei konnte nicht geöffnet werden");
        }
    }

    /**
     * Schließt den aktuell verwendeten Reader und Writer.
     *
     * Existiert ein Writer, wird dieser geschlossen. Dabei werden
     * noch nicht geschriebene Daten ebenfalls in die Datei übertragen.
     *
     * Existiert ein Reader, wird dieser ebenfalls geschlossen.
     *
     * @throws DateiNichtGefundenException wenn der Reader nicht
     *                                     geschlossen werden kann
     */
    public void close() throws DateiNichtGefundenException {
        // Writer schließen, falls einer geöffnet wurde.
        if (writer != null)
            writer.close();

        // Reader schließen, falls einer geöffnet wurde.
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new DateiNichtGefundenException("Datei konnte nicht geschlossen werden.");
            }
        }
    }

    /**
     * Lädt einen einzelnen Artikel aus der aktuell geöffneten Datei.
     *
     * Ein Artikel wird in mehreren aufeinanderfolgenden Zeilen
     * gespeichert. Das erwartete Format lautet:
     *
     * <pre>
     * Artikel-ID
     * Bezeichnung
     * Preis
     * Packungsgröße
     * </pre>
     *
     * Ist die Packungsgröße gleich {@code 1}, wird ein normaler
     * {@link Artikel} erzeugt. Bei einer anderen Packungsgröße
     * wird ein {@link Massengutartikel} erzeugt.
     *
     * @return geladener Artikel oder {@code null}, wenn keine
     *         weiteren Daten vorhanden sind
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     gelesen werden kann
     */
    public Artikel ladeArtikel() throws DateiNichtGefundenException {
        // Erste Zeile eines Artikeldatensatzes lesen.
        String artikelIDString = liesZeile();

        /*
         * Ist keine weitere Zeile vorhanden, sind alle
         * Artikeldaten vollständig gelesen.
         */
        if (artikelIDString == null) {
            return null;
        }

        // Artikel-ID aus dem gespeicherten Text erzeugen.
        int artikelID = Integer.parseInt(artikelIDString);

        // Artikelbezeichnung lesen.
        String bezeichnung = liesZeile();

        // Preis als Text lesen und in BigDecimal umwandeln.
        String preisString = liesZeile();
        BigDecimal preis = new BigDecimal(preisString);

        // Gespeicherte Packungsgröße lesen.
        int packungGroesse = Integer.parseInt(liesZeile());

        /*
         * Eine Packungsgröße von 1 kennzeichnet einen normalen Artikel.
         * Andernfalls wird ein Massengutartikel erzeugt.
         */
        return packungGroesse == 1 ? new Artikel(artikelID, bezeichnung, preis) : new Massengutartikel(artikelID, bezeichnung, preis, packungGroesse);
    }

    /**
     * Speichert einen einzelnen Artikel in der aktuell
     * geöffneten Datei.
     *
     * Die Eigenschaften des Artikels werden zeilenweise in
     * folgendem Format gespeichert:
     *
     * <pre>
     * Artikel-ID
     * Bezeichnung
     * Preis
     * Packungsgröße
     * </pre>
     *
     * Für normale Artikel wird als Packungsgröße der Wert
     * {@code 1} gespeichert.
     *
     * @param a zu speichernder Artikel
     * @throws DateiNichtGefundenException wenn die Daten nicht
     *                                     geschrieben werden können
     */
    public void speichereArtikel(Artikel a) throws DateiNichtGefundenException {
        // Artikel-ID speichern.
        schreibeZeile(String.valueOf(a.getArtikelID()));

        // Bezeichnung speichern.
        schreibeZeile(a.getBezeichnung());

        // Preis speichern.
        schreibeZeile(String.valueOf(a.getPreis()));

        /*
         * Bei Massengutartikeln wird die tatsächliche
         * Packungsgröße gespeichert.
         */
        if (a instanceof Massengutartikel) {
            schreibeZeile(String.valueOf(((Massengutartikel) a).getPackungGroesse()));
        } else {
            /*
             * Bei normalen Artikeln wird die Packungsgröße 1
             * als Kennzeichnung gespeichert.
             */
            schreibeZeile("1");
        }
    }

    /**
     * Lädt alle gespeicherten Artikelbestände.
     *
     * Jeder Bestand wird in einer einzelnen Zeile im folgenden
     * Format gespeichert:
     *
     * <pre>
     * Artikel-ID:Bestand
     * </pre>
     *
     * Das Ende der Bestandsdaten wird durch die Zeile
     * {@code EOF} gekennzeichnet.
     *
     * @return Map mit Artikel-IDs und den zugehörigen Beständen
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     gelesen werden kann
     */
    public HashMap<Integer, Integer> ladeArtikelMenge() throws DateiNichtGefundenException {
        // Leere Map für die geladenen Bestände erzeugen.
        HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();

        while (true) {
            // Nächste Bestandszeile lesen.
            String bestandString = liesZeile();

            /*
             * Beim Dateiende oder bei der Kennzeichnung EOF
             * werden die bisher geladenen Daten zurückgegeben.
             */
            if (bestandString == null || bestandString.equals("EOF")) {
                return artikelMengeListe;
            }

            /*
             * Artikel-ID und Bestand aus der durch einen
             * Doppelpunkt getrennten Zeile auslesen.
             */
            int artikelID = Integer.parseInt(bestandString.split(":")[0]);
            int bestand = Integer.parseInt(bestandString.split(":")[1]);

            /*
             * Zeilen ohne Doppelpunkt werden laut ursprünglicher
             * Programmlogik übersprungen.
             */
            if (!bestandString.contains(":")) continue;

            // Artikelbestand in der Map speichern.
            artikelMengeListe.put(artikelID, bestand);
        }
    }

    /**
     * Speichert alle Artikelbestände in der aktuell
     * geöffneten Datei.
     *
     * Jeder Eintrag wird im folgenden Format gespeichert:
     *
     * <pre>
     * Artikel-ID:Bestand
     * </pre>
     *
     * Nach dem letzten Eintrag wird die Zeile {@code EOF}
     * als Endemarkierung geschrieben.
     *
     * @param hm Map mit Artikel-IDs und Bestandsmengen
     * @throws DateiNichtGefundenException wenn die Daten nicht
     *                                     geschrieben werden können
     */
    public void speichereArtikelMenge(HashMap<Integer, Integer> hm) throws DateiNichtGefundenException {
        // Alle Bestände durchlaufen.
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            int artikelID = entry.getKey();
            int artikelBestand = entry.getValue();
            // Artikel-ID und Bestand gemeinsam speichern.
            schreibeZeile(artikelID + ":" + artikelBestand);
        }

        // Ende der Bestandsdatei kennzeichnen.
        schreibeZeile("EOF");
    }

    /**
     * Lädt alle Ereignisse aus der aktuell geöffneten Datei.
     *
     * Die Ereignisse werden anhand eines regulären Ausdrucks
     * aus Textzeilen ausgelesen.
     *
     * Das erwartete Format entspricht:
     *
     * <pre>
     * Tag: Datum |
     * Typ: Ereignistyp |
     * ArtikelID: ID |
     * Artikel: Bezeichnung |
     * Menge: Menge |
     * Person: Person
     * </pre>
     *
     * Für jedes erfolgreich erkannte Ereignis wird ein
     * {@link PersistenceManager.einEreignisInfo}-Objekt erzeugt.
     *
     * @return Liste mit den geladenen Ereignisinformationen
     * @throws DateiNichtGefundenException wenn die Datei nicht
     *                                     gelesen werden kann
     */
    @Override
    public ArrayList<PersistenceManager.einEreignisInfo> ladeEreignisse() throws DateiNichtGefundenException {
        // Ergebnisliste für geladene Ereignisse.
        ArrayList<einEreignisInfo> liste = new ArrayList<>();

        /*
         * Regulärer Ausdruck zum Erkennen der einzelnen
         * Bestandteile eines gespeicherten Ereignisses.
         */
        String regex = "Tag:\\s*(?<date>[^|]+?)\\s*\\|\\s*" +
                "Typ:\\s*(?<typ>[^|]+?)\\s*\\|\\s*" +
                "ArtikelID:\\s*(?<artikelID>[^|]+?)\\s*\\|\\s*" +
                "Artikel:\\s*(?<artikel>[^|]+?)\\s*\\|\\s*" +
                "Menge:\\s*(?<menge>\\d+)\\s*\\|\\s*" +
                "Person:\\s*(?<person>.+)";

        // Regulären Ausdruck kompilieren.
        Pattern pattern = Pattern.compile(regex);
        String line;

        // Ereignisdatei zeilenweise lesen.
        while ((line = liesZeile()) != null) {
            // Leere Zeilen ignorieren.
            if (line.trim().isEmpty()) continue;

            // Aktuelle Zeile mit dem erwarteten Muster vergleichen.
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                // Datum aus dem benannten Regex-Bereich lesen.
                LocalDate date = LocalDate.parse(matcher.group("date").trim());

                // Ereignistyp lesen.
                String typ = matcher.group("typ").trim();

                // Artikel-ID lesen und in eine Zahl umwandeln.
                int artikelID = Integer.parseInt(matcher.group("artikelID").trim());

                // Artikelbezeichnung lesen.
                String bezeichnung = matcher.group("artikel").trim();

                // Menge lesen und in eine Zahl umwandeln.
                int menge = Integer.parseInt(matcher.group("menge").trim());

                // Ausführende Person lesen.
                String person = matcher.group("person").trim();

                /*
                 * Aus den eingelesenen Daten ein
                 * Ereignisinformationsobjekt erzeugen.
                 */
                liste.add(new einEreignisInfo(date, artikelID, bezeichnung, menge, typ, person));
            }
        }

        return liste;
    }

    /**
     * Speichert eine Liste von Ereignissen in der aktuell
     * geöffneten Datei.
     *
     * Jedes Ereignis wird über seine {@code toString()}-Darstellung
     * in einer eigenen Zeile gespeichert.
     *
     * @param ereignisse zu speichernde Ereignisliste
     * @throws DateiNichtGefundenException wenn die Ereignisse
     *                                     nicht geschrieben werden können
     */
    @Override
    public void speichereEreignis(ArrayList<Ereignis> ereignisse) throws DateiNichtGefundenException {
        // Alle Ereignisse nacheinander speichern.
        for (Ereignis ereignis : ereignisse) {
            schreibeZeile(ereignis.toString());
        }
    }

    /**
     * Speichert alle Benutzer in der aktuell geöffneten Datei.
     *
     * Jeder Benutzer wird in einer einzelnen Zeile gespeichert.
     * Die Eigenschaften werden durch Semikolons getrennt:
     *
     * <pre>
     * Benutzer-ID;Benutzerkennung;Vor- und Nachname;Passwort;Rolle
     * </pre>
     *
     * @param benutzerMap Map mit allen zu speichernden Benutzern
     * @throws DateiNichtGefundenException wenn die Benutzerdaten
     *                                     nicht geschrieben werden können
     */
    @Override
    public void speicherBenutzer(HashMap<String, Benutzer> benutzerMap) throws DateiNichtGefundenException {
        // Alle Benutzer der Map durchlaufen.
        for (Benutzer benutzer : benutzerMap.values()) {

            /*
             * Benutzerdaten zu einer durch Semikolons
             * getrennten Textzeile zusammensetzen.
             */
            schreibeZeile(
                    benutzer.getBenutzerId() + ";" +
                            benutzer.getBenutzerErkennung() + ";" +
                            benutzer.getBenutzerVorNachname() + ";" +
                            benutzer.getBenutzerPassword() + ";" +
                            benutzer.getRole()
            );
        }
    }

    /**
     * Liest eine einzelne Zeile aus der aktuell
     * geöffneten Datei.
     *
     * Ist kein Reader vorhanden, wird ein leerer String
     * zurückgegeben.
     *
     * @return gelesene Zeile, {@code null} am Dateiende
     *         oder ein leerer String, wenn kein Reader geöffnet ist
     * @throws DateiNichtGefundenException wenn die Zeile nicht
     *                                     gelesen werden kann
     */
    private String liesZeile() throws DateiNichtGefundenException {
        if (reader != null)
            try {
                return reader.readLine();
            } catch (IOException e) {
                throw new DateiNichtGefundenException("Input konnte nicht gelesen werden");
            }
        else
            return "";
    }

    /**
     * Schreibt eine einzelne Zeile in die aktuell
     * geöffnete Datei.
     *
     * Ist kein Writer geöffnet, führt die Methode keine
     * Aktion aus.
     *
     * @param daten zu schreibende Datenzeile
     * @throws DateiNichtGefundenException laut Persistenzschnittstelle
     */
    private void schreibeZeile(String daten) throws DateiNichtGefundenException {
        // Zeile nur schreiben, wenn ein Writer vorhanden ist.
        if (writer != null)
            writer.println(daten);
    }
}
