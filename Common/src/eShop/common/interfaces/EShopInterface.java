package eShop.common.interfaces;

import eShop.common.entities.Artikel;
import eShop.common.entities.Benutzer;
import eShop.common.entities.Ereignis;
import eShop.common.exceptions.ArtikelExistiertNichtException;
import eShop.common.exceptions.BestandNichtAusreichendException;
import eShop.common.exceptions.DateiNichtGefundenException;
import eShop.common.exceptions.MassengutartikelmengeNichtTeilbarException;
import eShop.common.exceptions.MengeWenigerAlsPackungGroesseException;
import eShop.common.exceptions.UngueltigeMengeException;
import eShop.common.exceptions.UngueltigerPreisException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Definiert die gemeinsamen Funktionen des eShops.
 *
 * Die Schnittstelle beschreibt alle Operationen, die von einer
 * eShop-Implementierung bereitgestellt werden müssen.
 *
 * Dazu gehören:
 * <ul>
 *     <li>Benutzerverwaltung und Anmeldung,</li>
 *     <li>Artikel- und Bestandsverwaltung,</li>
 *     <li>Warenkorbverwaltung,</li>
 *     <li>Ereignis- und Bestandshistorie,</li>
 *     <li>das Beenden der Verbindung zum Server.</li>
 * </ul>
 *
 * Die Schnittstelle kann sowohl von einer lokalen eShop-Klasse
 * als auch von einer Client-Server-Implementierung verwendet werden.
 */
public interface EShopInterface {

    /**
     * Meldet einen Benutzer mit seiner Benutzerkennung
     * und seinem Passwort am eShop an.
     *
     * @param benutzerErkennung eindeutige Kennung des Benutzers
     * @param benutzerPasswort Passwort des Benutzers
     * @return {@code true}, wenn die Anmeldung erfolgreich war,
     *         sonst {@code false}
     */
    boolean login(
            String benutzerErkennung,
            String benutzerPasswort
    );

    /**
     * Meldet den aktuell angemeldeten Benutzer ab.
     */
    void logout();

    /**
     * Prüft, ob aktuell ein Benutzer angemeldet ist.
     *
     * @return {@code true}, wenn ein Benutzer angemeldet ist,
     *         sonst {@code false}
     */
    boolean istEingeloggt();

    /**
     * Prüft, ob der aktuell angemeldete Benutzer
     * die Rolle Mitarbeiter besitzt.
     *
     * @return {@code true}, wenn der Benutzer Mitarbeiter ist,
     *         sonst {@code false}
     */
    boolean istMitarbeiter();

    /**
     * Prüft, ob der aktuell angemeldete Benutzer
     * die Rolle Kunde besitzt.
     *
     * @return {@code true}, wenn der Benutzer Kunde ist,
     *         sonst {@code false}
     */
    boolean istKunde();

    /**
     * Gibt den aktuell angemeldeten Benutzer zurück.
     *
     * @return aktuell angemeldeter Benutzer oder {@code null},
     *         wenn niemand angemeldet ist
     */
    Benutzer aktuellerBenutzer();

    /**
     * Registriert einen neuen Benutzer im eShop.
     *
     * @param benutzer zu registrierender Benutzer
     * @return {@code true}, wenn die Registrierung erfolgreich war,
     *         sonst {@code false}
     */
    boolean registrieren(Benutzer benutzer);

    /**
     * Erzeugt eine neue eindeutige Benutzer-ID.
     *
     * @return neue Benutzer-ID
     */
    int generiereId();

    /**
     * Erzeugt eine neue eindeutige Artikel-ID.
     *
     * @return neue Benutzer-ID
     */
    int generiereArtikelID();

    /**
     * Gibt alle im eShop vorhandenen Artikel zurück.
     *
     * Der Schlüssel der Map ist die Artikel-ID.
     *
     * @return Map mit allen Artikeln
     */
    HashMap<Integer, Artikel> gibArtikelListe();

    /**
     * Gibt die Bestände aller Artikel zurück.
     *
     * Der Schlüssel der Map ist die Artikel-ID und der
     * zugehörige Wert ist die vorhandene Menge.
     *
     * @return Map mit allen Artikelbeständen
     */
    HashMap<Integer, Integer> gibArtikelMengeListe();

    /**
     * Fügt einen normalen Artikel in das Sortiment ein.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param bezeichnung Bezeichnung des Artikels
     * @param menge Anfangsbestand des Artikels
     * @param preis Preis des Artikels
     * @param mitarbeiter Name oder Kennung des Mitarbeiters,
     *                    der die Aktion ausführt
     * @throws DateiNichtGefundenException wenn die Artikeldaten
     *                                     nicht gespeichert werden können
     * @throws UngueltigerPreisException wenn der Preis ungültig ist
     * @throws UngueltigeMengeException wenn die Menge ungültig ist
     */
    void fuegeArtikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            BigDecimal preis,
            String mitarbeiter
    ) throws DateiNichtGefundenException,
            UngueltigerPreisException,
            UngueltigeMengeException;

    /**
     * Fügt einen Massengutartikel in das Sortiment ein.
     *
     * Ein Massengutartikel darf nur in Mengen verwaltet werden,
     * die zu seiner Packungsgröße passen.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param bezeichnung Bezeichnung des Artikels
     * @param menge Anfangsbestand des Artikels
     * @param preis Preis des Artikels
     * @param mitarbeiter Name oder Kennung des Mitarbeiters,
     *                    der die Aktion ausführt
     * @param packungGroesse Größe einer Packung
     * @throws UngueltigeMengeException wenn die Menge ungültig ist
     * @throws UngueltigerPreisException wenn der Preis ungültig ist
     * @throws MengeWenigerAlsPackungGroesseException wenn die Menge
     *         kleiner als eine Packung ist
     * @throws MassengutartikelmengeNichtTeilbarException wenn die Menge
     *         nicht ohne Rest durch die Packungsgröße teilbar ist
     * @throws DateiNichtGefundenException wenn die Artikeldaten
     *                                     nicht gespeichert werden können
     */
    void fuegeMassengutartikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            BigDecimal preis,
            String mitarbeiter,
            int packungGroesse
    ) throws UngueltigeMengeException,
            UngueltigerPreisException,
            MengeWenigerAlsPackungGroesseException,
            MassengutartikelmengeNichtTeilbarException,
            DateiNichtGefundenException;

    /**
     * Entfernt einen Artikel vollständig aus dem Sortiment.
     *
     * @param artikelID eindeutige ID des zu entfernenden Artikels
     */
    void artikelVernichten(int artikelID);

    /**
     * Ändert die Bezeichnung eines Artikels.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param bezeichnung neue Bezeichnung des Artikels
     * @throws DateiNichtGefundenException wenn die Änderung
     *                                     nicht gespeichert werden kann
     */
    void bezeichnungVeraendern(
            int artikelID,
            String bezeichnung
    ) throws DateiNichtGefundenException;

    /**
     * Ändert den Preis eines Artikels.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param preis neuer Preis des Artikels
     * @throws DateiNichtGefundenException wenn die Änderung
     *                                     nicht gespeichert werden kann
     * @throws UngueltigerPreisException wenn der neue Preis ungültig ist
     */
    void preisVeraendern(
            int artikelID,
            BigDecimal preis
    ) throws DateiNichtGefundenException,
            UngueltigerPreisException;

    /**
     * Sucht die ID eines Artikels anhand seiner Bezeichnung.
     *
     * @param bezeichnung Bezeichnung des gesuchten Artikels
     * @return ID des gefundenen Artikels
     * @throws ArtikelExistiertNichtException wenn kein Artikel mit
     *                                       dieser Bezeichnung existiert
     */
    int sucheNachID(
            String bezeichnung
    ) throws ArtikelExistiertNichtException;

    /**
     * Setzt den Bestand eines Artikels auf einen neuen Wert.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param neuerBestand gewünschter neuer Bestand
     * @param mitarbeiter Name oder Kennung des Mitarbeiters,
     *                    der die Änderung ausführt
     * @throws MengeWenigerAlsPackungGroesseException wenn der Bestand
     *         kleiner als die Packungsgröße ist
     * @throws MassengutartikelmengeNichtTeilbarException wenn der Bestand
     *         nicht durch die Packungsgröße teilbar ist
     * @throws DateiNichtGefundenException wenn die Änderung
     *                                     nicht gespeichert werden kann
     * @throws UngueltigeMengeException wenn der neue Bestand ungültig ist
     */
    void bestandVeraendern(
            int artikelID,
            int neuerBestand,
            String mitarbeiter
    ) throws MengeWenigerAlsPackungGroesseException,
            MassengutartikelmengeNichtTeilbarException,
            DateiNichtGefundenException,
            UngueltigeMengeException;

    /**
     * Prüft, ob ein Artikel ein Massengutartikel ist.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return {@code true}, wenn der Artikel ein Massengutartikel ist,
     *         sonst {@code false}
     */
    boolean istMassengutartikel(int artikelID);

    /**
     * Gibt die Packungsgröße eines Massengutartikels zurück.
     *
     * @param artikelID eindeutige ID des Massengutartikels
     * @return Packungsgröße des Artikels
     */
    int gibPackungGroesse(int artikelID);

    /**
     * Ändert die Packungsgröße eines Massengutartikels.
     *
     * @param artikelID eindeutige ID des Massengutartikels
     * @param neueGroesse neue Packungsgröße
     * @throws MassengutartikelmengeNichtTeilbarException wenn der aktuelle
     *         Bestand nicht durch die neue Packungsgröße teilbar ist
     */
    void packungGroesseVeraendern(
            int artikelID,
            int neueGroesse
    ) throws MassengutartikelmengeNichtTeilbarException;

    /**
     * Gibt den aktuellen Bestand eines Artikels zurück.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return aktueller Artikelbestand
     */
    int gibBestand(int artikelID);

    /**
     * Gibt die Bezeichnung eines Artikels zurück.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return Bezeichnung des Artikels
     */
    String gibArtikelName(int artikelID);

    /**
     * Sucht einen Artikel anhand seiner ID.
     *
     * @param artikelID eindeutige ID des gesuchten Artikels
     * @return gefundener Artikel oder {@code null},
     *         wenn der Artikel nicht existiert
     */
    Artikel findeArtikel(int artikelID);

    /**
     * Gibt den Preis eines Artikels zurück.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return Preis des Artikels
     */
    BigDecimal gibPreis(int artikelID);

    /**
     * Gibt den aktuellen Inhalt des Warenkorbs zurück.
     *
     * Der Schlüssel der Map entspricht der Artikel-ID.
     * Der Wert entspricht der Menge des Artikels im Warenkorb.
     *
     * @return Inhalt des Warenkorbs
     */
    HashMap<Integer, Integer> gibWarenkorb();

    /**
     * Fügt eine bestimmte Menge eines Artikels zum Warenkorb hinzu.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge gewünschte Menge
     * @param kunde Name oder Kennung des Kunden
     * @throws UngueltigeMengeException wenn die Menge ungültig ist
     * @throws BestandNichtAusreichendException wenn der verfügbare
     *         Bestand nicht ausreicht
     * @throws MengeWenigerAlsPackungGroesseException wenn die Menge
     *         kleiner als die Packungsgröße ist
     * @throws MassengutartikelmengeNichtTeilbarException wenn die Menge
     *         nicht durch die Packungsgröße teilbar ist
     * @throws DateiNichtGefundenException wenn das Ereignis
     *                                     nicht gespeichert werden kann
     */
    void fuegeInWarenkorb(
            int artikelID,
            int menge,
            String kunde
    ) throws UngueltigeMengeException,
            BestandNichtAusreichendException,
            MengeWenigerAlsPackungGroesseException,
            MassengutartikelmengeNichtTeilbarException,
            DateiNichtGefundenException;

    /**
     * Entfernt eine bestimmte Menge eines Artikels aus dem Warenkorb.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge zu entfernende Menge
     * @param kunde Name oder Kennung des Kunden
     * @throws UngueltigeMengeException wenn die Menge ungültig ist
     * @throws MengeWenigerAlsPackungGroesseException wenn die Menge
     *         kleiner als die Packungsgröße ist
     * @throws MassengutartikelmengeNichtTeilbarException wenn die Menge
     *         nicht durch die Packungsgröße teilbar ist
     * @throws DateiNichtGefundenException wenn das Ereignis
     *                                     nicht gespeichert werden kann
     */
    void loescheAusWarenkorb(
            int artikelID,
            int menge,
            String kunde
    ) throws UngueltigeMengeException,
            MengeWenigerAlsPackungGroesseException,
            MassengutartikelmengeNichtTeilbarException,
            DateiNichtGefundenException;

    /**
     * Entfernt alle Artikel aus dem Warenkorb.
     */
    void zuruecksetzeWarenkorb();

    /**
     * Gibt alle gespeicherten Ereignisse des eShops zurück.
     *
     * Dazu gehören beispielsweise Einlagerungen,
     * Auslagerungen und Änderungen am Warenkorb.
     *
     * @return Liste aller Ereignisse
     */
    ArrayList<Ereignis> gibEreignisListe();

    /**
     * Berechnet die Bestandshistorie eines Artikels.
     *
     * Jedem Datum wird der zu diesem Zeitpunkt berechnete
     * Bestand des Artikels zugeordnet.
     *
     * @param artikelID eindeutige ID des Artikels
     * @return chronologische Zuordnung von Datum und Bestand
     */
    Map<LocalDate, Integer> berechneBestandHistorie(int artikelID);
}