package eShop.server.domain;

import eShop.common.exceptions.DateiNichtGefundenException;
import eShop.common.entities.Benutzer;
import eShop.server.persistence.FilePersistenceManager;
import eShop.server.persistence.PersistenceManager;

import java.io.*;
import java.util.HashMap;

/**
 * Verwaltet alle Benutzer des eShops.
 *
 * Die Klasse übernimmt die Registrierung,
 * Anmeldung und Abmeldung von Benutzern.
 * Außerdem verwaltet sie den aktuell
 * angemeldeten Benutzer sowie die Vergabe
 * neuer Benutzer-IDs.
 *
 * Die Abkürzung {@code VW} steht für Verwaltung.
 */
public class BenutzerVW {
    /**
     * Name der Datei, in der die Benutzerdaten gespeichert werden.
     */
    private final String datei = "benutzer.txt"; //TODO: nicht so hardcoded!

    /**
     * Der aktuell angemeldete Benutzer.
     */
    private Benutzer aktuellerBenutzer;

    /**
     * Nächste freie Benutzer-ID.
     */
    private int nextId = 1;

    /**
     * Persistenzmanager zum Laden und Speichern der Benutzerdaten.
     */
    private PersistenceManager pm = new FilePersistenceManager();

    /**
     * Enthält alle registrierten Benutzer.
     *
     * Der Schlüssel ist die Benutzerkennung.
     */
    private HashMap<String, Benutzer> benutzerMap = new HashMap<>();

    /**
     * Erstellt eine neue Benutzerverwaltung.
     *
     * Beim Erzeugen werden alle gespeicherten Benutzer geladen
     * und die nächste freie Benutzer-ID bestimmt.
     *
     * @throws DateiNichtGefundenException wenn die Benutzerdaten
     *                                     nicht geladen werden können
     */
    public BenutzerVW() throws DateiNichtGefundenException {
        try {
            // Benutzer aus der Datei laden.
            this.benutzerMap = this.pm.ladeBenutzer();

            // Höchste vorhandene ID suchen.
            for (Benutzer benutzer : benutzerMap.values()) {
                nextId = Math.max(nextId, benutzer.getBenutzerId() + 1);
            }
        } catch (IOException e){
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Registriert einen neuen Benutzer.
     *
     * Existiert bereits ein Benutzer mit derselben
     * Benutzerkennung, wird keine Registrierung durchgeführt.
     *
     * Nach erfolgreicher Registrierung werden alle Benutzerdaten
     * dauerhaft gespeichert.
     *
     * @param benutzer zu registrierender Benutzer
     * @return {@code true}, wenn die Registrierung erfolgreich war,
     *         sonst {@code false}
     * @throws DateiNichtGefundenException wenn die Benutzerdaten
     *                                     nicht gespeichert werden können
     */
    public boolean registrieren(Benutzer benutzer) throws DateiNichtGefundenException {
        // Benutzerkennung existiert bereits.
        if (this.benutzerMap.containsKey(benutzer.getBenutzerErkennung())) {
            return false;
        }

        // Benutzer hinzufügen.
        this.benutzerMap.put(benutzer.getBenutzerErkennung(), benutzer);

        try {
            // Benutzerdaten speichern.
            pm.openForWriting(datei);
            pm.speicherBenutzer(benutzerMap);
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }

        return true;
    }

    /**
     * Meldet einen Benutzer am System an.
     *
     * Die Anmeldung ist erfolgreich, wenn ein Benutzer mit der
     * angegebenen Benutzerkennung existiert und das Passwort
     * korrekt ist.
     *
     * @param benutzerErkennung Benutzerkennung
     * @param benutzerPassword Passwort
     * @return {@code true}, wenn die Anmeldung erfolgreich war,
     *         sonst {@code false}
     */
    public boolean login(String benutzerErkennung, String benutzerPassword) {
        Benutzer benutzer = (Benutzer)this.benutzerMap.get(benutzerErkennung);
        if (benutzer != null && benutzer.checkPassword(benutzerPassword)) {
            this.aktuellerBenutzer = benutzer;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Meldet den aktuell angemeldeten Benutzer ab.
     */
    public void logout() {
        if (this.aktuellerBenutzer != null) {
            this.aktuellerBenutzer = null;
        }
    }

    /**
     * Gibt den aktuell angemeldeten Benutzer zurück.
     *
     * @return aktuell angemeldeter Benutzer oder {@code null}
     */
    protected Benutzer getAktuellerBenutzer() {
        return this.aktuellerBenutzer;
    }

    /**
     * Prüft, ob momentan ein Benutzer angemeldet ist.
     *
     * @return {@code true}, wenn ein Benutzer angemeldet ist,
     *         sonst {@code false}
     */
    public boolean istEingeloggt() {
        return this.aktuellerBenutzer != null;
    }

    /**
     * Prüft, ob der aktuell angemeldete Benutzer Mitarbeiter ist.
     *
     * @return {@code true}, wenn der Benutzer Mitarbeiter ist,
     *         sonst {@code false}
     */
    public boolean istMitarbeiter() {
        return this.aktuellerBenutzer != null && this.aktuellerBenutzer.getRole().equals("Mitarbeiter");
    }

    /**
     * Prüft, ob der aktuell angemeldete Benutzer Kunde ist.
     *
     * @return {@code true}, wenn der Benutzer Kunde ist,
     *         sonst {@code false}
     */
    public boolean istKunde() {
        return this.aktuellerBenutzer != null && this.aktuellerBenutzer.getRole().equals("Kunde");
    }

    /**
     * Erzeugt eine neue eindeutige Benutzer-ID.
     *
     * Nach jedem Aufruf wird die interne ID erhöht.
     *
     * @return neue Benutzer-ID
     */
    public int generiereId(){
        return nextId++;
    }
}
