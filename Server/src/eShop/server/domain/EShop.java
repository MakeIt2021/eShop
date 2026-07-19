package eShop.server.domain;

import eShop.common.entities.*;
import eShop.common.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Die zentrale Server-Implementierung der eShop-Geschäftslogik.
 * Verknüpft die einzelnen Verwaltungskomponenten (Artikel, Benutzer, Ereignisse, Warenkörbe)
 * und stellt die Kernfunktionalität für Clients über das {@link eShop.common.interfaces.EShopInterface} bereit.
 */

public class EShop implements eShop.common.interfaces.EShopInterface {
    /** Pfad und Basisname der Textdateien für die Artikel- und Bestandsdaten. */
    private final String datei = "Server/resources/eShop";

    /** Die interne Verwaltungskomponente für Artikel und deren Bestände. */
    private final ArtikelVW artikelVW;

    /** Die interne Verwaltungskomponente für die Benutzerregistrierung und Authentifizierung. */
    private final BenutzerVW benutzerVW;

    /** Die interne Verwaltungskomponente für das Logging von Systemereignissen. */
    private final EreignisVW ereignisVW;

    /** Standard-Schlüsselname für den anonymen oder nicht zugeordneten Warenkorb. */
    private static final String STANDARD_WARENKORB = "__standard__";

    /** Eine thread-sichere Map zur Verwaltung aller aktiven Kunden-Warenkörbe (Kundenname zu Warenkorb-Manager). */
    private final Map<String, WarenkorbVW> warenkoerbe;

    /**
     * Initialisiert den eShop und lädt die bestehenden Artikel- und Ereignisdaten aus den Textdateien.
     *
     * @throws DateiNichtGefundenException Wenn die Datenbestände nicht geladen werden können.
     */
    public EShop() throws DateiNichtGefundenException {
        artikelVW = new ArtikelVW();
        artikelVW.ladeArtikelMengeDaten(datei);
        warenkoerbe = new ConcurrentHashMap<>();
        benutzerVW = new BenutzerVW();
        ereignisVW = new EreignisVW();
        ereignisVW.ladeEreignisse("Server/resources/Ereignisse.txt", artikelID -> artikelVW.findeArtikel(Integer.parseInt(artikelID)));
    }

    /**
     * Gibt eine Kopie der aktuellen Artikelliste zurück.
     *
     * @return Eine {@link HashMap} mit allen im System registrierten Artikeln (ID zu Objekt).
     */
    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelVW.gibArtikelListe();
    }

    /**
     * Gibt die aktuellen Bestandsmengen aller Artikel zurück.
     *
     * @return Eine {@link HashMap}, die jeder Artikel-ID die aktuelle Lagermenge zuordnet.
     */
    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelVW.gibArtikelMengeListe();
    }

    /**
     * Ruft den Inhalt des Standard-Warenkorbs ab.
     *
     * @return Eine {@link HashMap} des Standard-Warenkorbs (Artikel-ID zu Menge).
     */
    public HashMap<Integer, Integer> gibWarenkorb() {
        return gibWarenkorb(STANDARD_WARENKORB);
    }

    /**
     * Ruft den Inhalt des Warenkorbs für einen spezifischen Kunden ab.
     *
     * @param kunde Der Benutzername oder die Kennung des Kunden.
     * @return Eine {@link HashMap} des kunden-spezifischen Warenkorbs (Artikel-ID zu Menge).
     */
    public HashMap<Integer, Integer> gibWarenkorb(String kunde) {
        return warenkorbFuer(kunde).gibWarenkorb();
    }

    /**
     * Fügt einen neuen Standard-Artikel in das System ein oder erhöht den Bestand, falls die ID bereits existiert.
     * Validiert den Preis und die Menge und erzeugt ein Einlagerungsereignis.
     *
     * @param artikelID Die eindeutige ID des neuen Artikels.
     * @param bezeichnung Der Name des Artikels.
     * @param menge Die einzulagernde Anfangsmenge.
     * @param preis Der Stückpreis des Artikels.
     * @param mitarbeiter Die Kennung des ausführenden Mitarbeiters für das Log.
     * @throws ArtikelExistiertBereitsException Wenn bereits ein Artikel unter diesem Namen registriert ist.
     * @throws UngueltigerPreisException Wenn der angegebene Preis negativ ist.
     * @throws UngueltigeMengeException Wenn die Menge kleiner oder gleich 0 ist.
     * @throws DateiNichtGefundenException Wenn beim Speichern der aktualisierten Daten ein Fehler auftritt.
     */
    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter) throws DateiNichtGefundenException {
        int gefundeneID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (gefundeneID != -1) {
            throw new ArtikelExistiertBereitsException(findeArtikel(gefundeneID));
        }

        Artikel art = new Artikel(artikelID, bezeichnung.toLowerCase(), preis);

        if (artikelVW.findeArtikel(artikelID) == null) {
            // NEUER ARTIKEL → in die Liste einfügen
            artikelVW.einfuegen(art, menge);
        } else {
            // Artikel existiert → Bestand erhöhen
            artikelVW.bestandErhoehen(artikelID, menge);
        }

        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }
        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        ereignisVW.addEreignis(art, menge, "Einlagerung", "m:" + mitarbeiter);

        speichereArtikel();
    }
    /**
     * Fügt einen neuen Massengutartikel in das System ein.
     * Überprüft, ob die Menge durch die Packungsgröße teilbar ist und die Mindestgröße erreicht.
     *
     * @param artikelID Die eindeutige ID des Massengutartikels.
     * @param bezeichnung Der Name des Artikels.
     * @param menge Die einzulagernde Anfangsmenge.
     * @param preis Der Stückpreis des Artikels.
     * @param mitarbeiter Die Kennung des ausführenden Mitarbeiters für das Log.
     * @param packungGroesse Die feste Verpackungseinheit (z. B. 5er, 10er Pack).
     * @throws UngueltigeMengeException Wenn die Menge kleiner oder gleich 0 ist.
     * @throws UngueltigerPreisException Wenn der Preis negativ ist.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Anfangsmenge kleiner als die Packungsgröße ist.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Menge kein Vielfaches der Packungsgröße ist.
     * @throws DateiNichtGefundenException Wenn beim Speichern der Daten ein Fehler auftritt.
     */
    public void fuegeMassengutartikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            BigDecimal preis,
            String mitarbeiter,
            int packungGroesse
    ) throws UngueltigeMengeException, UngueltigerPreisException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        int gefundeneID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);
        if (gefundeneID != -1) {
            throw new ArtikelExistiertBereitsException(findeArtikel(gefundeneID));
        }

        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (menge < packungGroesse) {
            throw new MengeWenigerAlsPackungGroesseException(
                    bezeichnung,
                    menge,
                    packungGroesse
            );
        }

        if (menge % packungGroesse != 0) {
            throw new MassengutartikelmengeNichtTeilbarException(
                    bezeichnung,
                    packungGroesse
            );
        }

        // Создаем и вставляем объект
        Artikel art = new Massengutartikel(
                artikelID,
                bezeichnung.toLowerCase(), // Приводим к нижнему регистру для консистентности с обычными товарами
                preis,
                packungGroesse
        );

        artikelVW.einfuegen(art, menge);
        ereignisVW.addEreignis(art, menge, "Einlagerung", "m:" + mitarbeiter);

        speichereArtikel();
    }

    /**
     * Löscht einen Artikel dauerhaft und vollständig aus dem Warensystem.
     *
     * @param artikelID Die ID des zu vernichtenden Artikels.
     */
    public void artikelVernichten(int artikelID) {
        artikelVW.artikelVernichten(artikelID);
        speichereArtikel();
    }

    /**
     * Ändert die Bezeichnung eines Artikels im System.
     * Verhindert Namensduplikate.
     *
     * @param artikelID Die ID des Zielartikels.
     * @param bezeichnung Der neue Wunschname für den Artikel.
     * @throws DateiNichtGefundenException Wenn die Speicherung fehlschlägt.
     * @throws ArtikelExistiertBereitsException Wenn der neue Name bereits von einem anderen Artikel belegt ist.
     */
    public void bezeichnungVeraendern(int artikelID, String bezeichnung) throws DateiNichtGefundenException {
        if (sucheNachID(bezeichnung) != artikelID) {
            throw new ArtikelExistiertBereitsException(findeArtikel(sucheNachID(bezeichnung)));
        }
        artikelVW.bezeichnungVeraendern(artikelID, bezeichnung);
        speichereArtikel();
    }

    /**
     * Ändert den Verkaufspreis eines Artikels.
     *
     * @param artikelID Die ID des Zielartikels.
     * @param preis Der neue Preis als {@link BigDecimal}.
     * @throws DateiNichtGefundenException Wenn das Speichern fehlschlägt.
     * @throws UngueltigerPreisException Wenn der übergebene Preis negativ ist.
     */
    public void preisVeraendern(int artikelID, BigDecimal preis) throws DateiNichtGefundenException {
        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }

        artikelVW.preisVeraendern(artikelID, preis);
        speichereArtikel();
    }

    /**
     * Reserviert eine gewünschte Menge eines Artikels im Warenkorb eines Kunden und verringert
     * den verfügbaren Hauptbestand im Lager.
     *
     * @param artikelID Die ID des Artikels.
     * @param menge Die gewünschte Bestellmenge.
     * @param kunde Der Name des Kunden, dem der Warenkorb gehört.
     * @throws UngueltigeMengeException Wenn die Menge kleiner oder gleich 0 ist.
     * @throws BestandNichtAusreichendException Wenn das Lager weniger Artikel hat als angefordert.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Menge bei Massengut unter der Packungsgröße liegt.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Menge nicht durch die Packungsgröße teilbar ist.
     * @throws DateiNichtGefundenException Wenn beim automatischen Speichern ein Fehler auftritt.
     */
    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, BestandNichtAusreichendException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (artikelVW.getBestand(artikelID) < menge) {
            throw new BestandNichtAusreichendException(
                    gibArtikelName(artikelID),
                    artikelVW.getBestand(artikelID),
                    menge
            );
        }

        if (istMassengutartikel(artikelID)) {
            if (menge < gibPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        gibArtikelName(artikelID),
                        menge,
                        gibPackungGroesse(artikelID)
                );
            }

            if (menge % gibPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        gibArtikelName(artikelID),
                        gibPackungGroesse(artikelID)
                );
            }
        }
        warenkorbFuer(kunde).einfuegen(artikelID, menge);
        artikelVW.bestandVerringern(artikelID, menge);
        ereignisVW.addEreignis(artikelVW.findeArtikel(artikelID), menge, "Auslagerung", "k:" + kunde);

        speichereArtikel();
    }

    /**
     * Entfernt eine Menge eines Artikels aus dem Kunden-Warenkorb und führt diese Menge
     * wieder direkt dem verfügbaren Lagerbestand zu.
     *
     * @param artikelID Die ID des Artikels.
     * @param menge Die zu entfernende Menge.
     * @param kunde Der Name des Kunden.
     * @throws UngueltigeMengeException Wenn die Menge ungültig oder negativ ist.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Menge die Packungsuntergrenze verletzt.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Menge nicht paketkonform ist.
     * @throws DateiNichtGefundenException Wenn die Dateiaktualisierung scheitert.
     */
    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (istMassengutartikel(artikelID)) {
            if (menge < gibPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(gibArtikelName(artikelID), menge, gibPackungGroesse(artikelID));
            }

            if (menge % gibPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), gibPackungGroesse(artikelID));
            }
        }

        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbFuer(kunde).loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);


        speichereArtikel();
    }

    /**
     * Leert den globalen Standard-Warenkorb vollständig.
     */
    public void zuruecksetzeWarenkorb() {
        zuruecksetzeWarenkorb(STANDARD_WARENKORB);
    }

    /**
     * Leert den Warenkorb eines spezifischen Kunden.
     *
     * @param kunde Der Name des Kunden.
     */
    public void zuruecksetzeWarenkorb(String kunde) {
        warenkorbFuer(kunde).zuruecksetzen();
    }

    /**
     * Hilfsmethode zur Ermittlung des passenden Warenkorb-Managers.
     *
     * @param kunde Der Name des Kunden.
     * @return Das zugeordnete {@link WarenkorbVW}-Objekt.
     */
    private WarenkorbVW warenkorbFuer(String kunde) {
        String schluessel = (kunde == null || kunde.isBlank()) ? STANDARD_WARENKORB : kunde;
        return warenkoerbe.computeIfAbsent(schluessel, ignoriert -> new WarenkorbVW());
    }

    /**
     * Schreibt den aktuellen Zustand der Artikeldaten und der Bestandsmengen in die Textdateien.
     *
     * @throws DateiNichtGefundenException Wenn die Zielpfade nicht beschreibbar sind.
     */
    public void speichereArtikel() throws DateiNichtGefundenException {
        artikelVW.speichereArtikelMengeDaten(datei+"_AM.txt");
        artikelVW.speichereArtikelDaten(datei+"_A.txt");
    }

    /**
     * Sucht nach einem Artikel anhand seines exakten Namens und liefert dessen ID zurück.
     *
     * @param bezeichnung Der Name des gesuchten Artikels.
     * @return Die ID des Artikels.
     * @throws ArtikelExistiertNichtException Wenn kein passender Artikel gefunden wurde.
     */
    public int sucheNachID(String bezeichnung) {
        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID == -1) {
            throw new ArtikelExistiertNichtException(bezeichnung);
        }
        return artikelID;
    }

    /**
     * Modifiziert den absoluten Lagerbestand eines Artikels manuell und erzeugt ein
     * passendes Ereignis (Ein- oder Auslagerung) basierend auf der Differenz.
     *
     * @param artikelID Die ID des Artikels.
     * @param neuerBestand Die neue absolute Zielmenge im Lager.
     * @param mitarbeiter Der Name des verantwortlichen Mitarbeiters.
     * @throws MengeWenigerAlsPackungGroesseException Bei Verletzung der Mindestmenge für Massengut.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn der Zielbestand nicht paketkonform ist.
     * @throws DateiNichtGefundenException Wenn das Speichern fehlschlägt.
     */
    public void bestandVeraendern(
            int artikelID,
            int neuerBestand,
            String mitarbeiter
    ) throws MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        if (neuerBestand <= 0) {
            throw new UngueltigeMengeException(neuerBestand);
        }

        if (istMassengutartikel(artikelID)) {
            if (neuerBestand < gibPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        gibArtikelName(artikelID),
                        neuerBestand,
                        gibPackungGroesse(artikelID)
                );
            }

            if (neuerBestand % gibPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        gibArtikelName(artikelID),
                        gibPackungGroesse(artikelID)
                );
            }
        }

        int aktuellerBestand = artikelVW.getBestand(artikelID);
        Artikel a = artikelVW.findeArtikel(artikelID);

        if (aktuellerBestand < neuerBestand) {
            artikelVW.bestandErhoehen(artikelID,neuerBestand - aktuellerBestand);

            ereignisVW.addEreignis(a, neuerBestand - aktuellerBestand, "Einlagerung", "m:" + mitarbeiter);
        } else {
            artikelVW.bestandVerringern(artikelID,aktuellerBestand - neuerBestand);

            ereignisVW.addEreignis(a, aktuellerBestand - neuerBestand, "Auslagerung", "m:" + mitarbeiter);
        }

        speichereArtikel();
    }

    /**
     * Prüft, ob es sich bei einer Artikel-ID um ein Massengut-Produkt handelt.
     *
     * @param artikelID Die ID des Artikels.
     * @return {@code true}, wenn es ein Massengutartikel ist, sonst {@code false}.
     */
    public boolean istMassengutartikel(int artikelID) {
        return artikelVW.istMassengutartikel(artikelID);
    }

    /**
     * Gibt die Verpackungseinheit eines Artikels zurück.
     *
     * @param artikelID Die ID des Artikels.
     * @return Die Paketgröße (Standardartikel liefern immer {@code 1}).
     */
    public int gibPackungGroesse(int artikelID) {
        if (istMassengutartikel(artikelID))
            return artikelVW.getPackungGroesse(artikelID);
        else
            return 1;
    }

    /**
     * Ändert die Packungsgröße eines Massengutartikels und validiert nachträglich
     * die logische Aufteilung des bereits vorhandenen Lagerbestands.
     *
     * @param artikelID Die ID des Massengutartikels.
     * @param neueGroesse Die neue Verpackungseinheit.
     */
    public void packungGroesseVeraendern(int artikelID, int neueGroesse) {
        artikelVW.packungGroesseVeraendern(artikelID, neueGroesse);
        if (gibBestand(artikelID) % neueGroesse != 0) {
            throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), neueGroesse);
        }
    }

    /**
     * Liefert die aktuelle Menge eines Artikels, die sich im Lager befindet.
     *
     * @param artikelID Die ID des Artikels.
     * @return Die Lagermenge als {@code int}.
     */
    public int gibBestand(int artikelID) {
        return artikelVW.getBestand(artikelID);
    }

    /**
     * Gibt den Namen/die Bezeichnung eines Artikels zurück.
     *
     * @param artikelID Die ID des Artikels.
     * @return Der Name des Artikels als String.
     */
    public String gibArtikelName(int artikelID) {
        return artikelVW.getArtikelName(artikelID);
    }

    /**
     * Führt die Benutzer-Authentifizierung über die Benutzerkomponente aus.
     *
     * @param benutzerErkennung Der Login-Name des Benutzers.
     * @param benutzerPasswort Das Passwort des Benutzers.
     * @return {@code true} bei korrekten Anmeldedaten, sonst {@code false}.
     */
    public boolean login (String benutzerErkennung, String benutzerPasswort) {
        return benutzerVW.login(benutzerErkennung, benutzerPasswort);

    }

    /**
     * Meldet den aktuellen Benutzer aus dem eShop-System ab.
     */
    public void logout () {
        benutzerVW.logout();
    }

    /**
     * Prüft den aktuellen Login-Status der laufenden Session.
     *
     * @return {@code true}, wenn ein User angemeldet ist, andernfalls {@code false}.
     */
    public boolean istEingeloggt(){
        return benutzerVW.istEingeloggt();
    }

    /**
     * Prüft, ob der angemeldete Benutzer Rechte eines System-Mitarbeiters besitzt.
     *
     * @return {@code true}, wenn ein Mitarbeiter eingeloggt ist.
     */
    public boolean istMitarbeiter(){
        return benutzerVW.istMitarbeiter();
    }

    /**
     * Prüft, ob der angemeldete Benutzer als regulärer Kunde eingeloggt ist.
     *
     * @return {@code true}, wenn ein Kunde eingeloggt ist.
     */
    public boolean istKunde(){
        return benutzerVW.istKunde();
    }

    /**
     * Registriert ein neues Benutzerobjekt (Kunde/Mitarbeiter) dauerhaft im System.
     *
     * @param benutzer Das vollständig ausgefüllte Benutzer-Entity-Objekt.
     * @return {@code true} bei erfolgreicher Registrierung, {@code false} bei Duplikaten.
     */
    public boolean registrieren (Benutzer benutzer){
        return benutzerVW.registrieren(benutzer);
    }

    /**
     * Generiert eine systemweit fortlaufende, freie Nummer für neue Accounts.
     *
     * @return Eine eindeutige ID als {@code int}.
     */
    public int generiereId() {
        return benutzerVW.generiereId();
    }

    /**
     * Generiert eine systemweit fortlaufende, freie Nummer für neue Artikel.
     *
     * @return Eine eindeutige ID als {@code int}.
     */
    public int generiereArtikelID() {
        return artikelVW.generiereArtikelID();
    }

    /**
     * Gibt das Benutzerobjekt des aktuell in dieser Sitzung angemeldeten Benutzers zurück.
     *
     * @return Das {@link Benutzer}-Objekt des aktuellen Nutzers oder {@code null}, wenn niemand eingeloggt ist.
     */
    public Benutzer aktuellerBenutzer () {
        return benutzerVW.getAktuellerBenutzer();
    }

    /**
     * Gibt das vollständige, chronologische System-Logbuch aller bisherigen Warenbewegungen zurück.
     *
     * @return Eine {@link ArrayList} mit allen registrierten {@link Ereignis}-Objekten.
     */
    public ArrayList<Ereignis> gibEreignisListe() {
        return ereignisVW.gibEreignisListe();
    }

    /**
     * Berechnet die historische Bestandsentwicklung eines spezifischen Artikels taggenau
     * für die letzten 30 Tage anhand der im System aufgezeichneten Ereignisse.
     *
     * @param artikelID Die eindeutige ID des zu analysierenden Artikels.
     * @return Eine zeitlich sortierte {@link Map}, die jedem Datum ({@link LocalDate}) den damaligen Lagerbestand ({@code Integer}) zuordnet.
     */
    public Map<LocalDate, Integer> berechneBestandHistorie(int artikelID) {
        return ereignisVW.gibBestandHistorie(artikelID);
    }

    /**
     * Sucht ein bestimmtes Artikelobjekt anhand seiner numerischen ID im Speicher.
     *
     * @param artikelID Die eindeutige ID des gesuchten Produkts.
     * @return Das passende {@link Artikel}-Objekt oder {@code null}, falls kein Artikel unter dieser ID existiert.
     */
    public Artikel findeArtikel(int artikelID) {
        return artikelVW.findeArtikel(artikelID);
    }

    /**
     * Ruft den aktuellen Preis eines Artikels ab.
     *
     * @param artikelID Die eindeutige ID des Artikels.
     * @return Der aktuelle Verkaufspreis als präziser {@link BigDecimal}-Wert.
     */
    public BigDecimal gibPreis(int artikelID) {
        return artikelVW.gibPreis(artikelID);
    }
}
