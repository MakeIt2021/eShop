package eShop.common.entities;

/**
 * Abstrakte Basisklasse für alle Benutzer des eShops.
 *
 * Ein Benutzer besitzt eine eindeutige ID,
 * eine Benutzerkennung, einen Vor- und Nachnamen
 * sowie ein Passwort.
 *
 * Von dieser Klasse erben die Klassen
 * {@link Kunde} und {@link Mitarbeiter}.
 */
public abstract class Benutzer {
    /**
     * Eindeutige ID des Benutzers.
     */
    private int benutzerId;

    /**
     * Benutzerkennung des Benutzers.
     */
    private String benutzerErkennung;

    /**
     * Vor- und Nachname des Benutzers.
     */
    private String benutzerVorNachname;

    /**
     * Passwort des Benutzers.
     */
    private String benutzerPassword;

    /**
     * Erstellt einen neuen Benutzer.
     *
     * @param benutzerId eindeutige Benutzer-ID
     * @param benutzerErkennung Benutzerkennung
     * @param benutzerVorNachname Vor- und Nachname
     * @param benutzerPassword Passwort
     */
    public Benutzer(
            int benutzerId,
            String benutzerErkennung,
            String benutzerVorNachname,
            String benutzerPassword
    ) {
        this.benutzerId = benutzerId;
        this.benutzerErkennung = benutzerErkennung;
        this.benutzerVorNachname = benutzerVorNachname;
        this.benutzerPassword = benutzerPassword;
    }

    /**
     * Gibt die Benutzerkennung zurück.
     *
     * @return Benutzerkennung
     */
    public String getBenutzerErkennung() {
        return this.benutzerErkennung;
    }

    /**
     * Überprüft, ob das angegebene Passwort
     * mit dem gespeicherten Passwort übereinstimmt.
     *
     * @param benutzerPassword zu überprüfendes Passwort
     * @return {@code true}, wenn das Passwort korrekt ist,
     *         sonst {@code false}
     */
    public boolean checkPassword(String benutzerPassword) {
        return this.benutzerPassword.equals(benutzerPassword);
    }

    /**
     * Gibt das Passwort zurück.
     *
     * @return Passwort
     */
    public String getBenutzerPassword() {
        return this.benutzerPassword;
    }

    /**
     * Gibt den Vor- und Nachnamen zurück.
     *
     * @return Vor- und Nachname
     */
    public String getBenutzerVorNachname() {
        return this.benutzerVorNachname;
    }

    /**
     * Gibt die Benutzer-ID zurück.
     *
     * @return Benutzer-ID
     */
    public int getBenutzerId() {
        return this.benutzerId;
    }

    /**
     * Gibt die Rolle des Benutzers zurück.
     *
     * Diese Methode muss von jeder Unterklasse
     * implementiert werden.
     *
     * @return Rolle des Benutzers
     */
    public abstract String getRole();

    /**
     * Erstellt eine Zeichenkette für die
     * Netzwerkübertragung.
     *
     * Alle Attribute werden durch Semikolons getrennt.
     *
     * @return Netzwerkdarstellung des Benutzers
     */
    public String toNetworkString() {
        return benutzerId
                + ";"
                + benutzerErkennung
                + ";"
                + benutzerVorNachname
                + ";"
                + benutzerPassword
                + ";"
                + getRole();
    }

    /**
     * Erstellt anhand einer Netzwerkdarstellung
     * ein Benutzerobjekt.
     *
     * Je nach gespeicherter Rolle wird ein
     * {@link Kunde} oder {@link Mitarbeiter}
     * erzeugt.
     *
     * @param line Netzwerkdarstellung eines Benutzers
     * @return erzeugtes Benutzerobjekt oder {@code null},
     *         falls keine gültigen Daten vorhanden sind
     */
    public static Benutzer fromNetworkString(String line) {
        // Keine Daten vorhanden.
        if (line == null || line.equals("null")) {
            return null;
        }

        // Zeichenkette in einzelne Bestandteile zerlegen.
        String[] parts = line.split(";");

        int id = Integer.parseInt(parts[0]);
        String benutzerErkennung = parts[1];
        String benutzerVorNachName = parts[2];
        String benutzerPasswort = parts[3];
        String role = parts[4];

        // Passendes Benutzerobjekt erzeugen.
        if (role.equals("Kunde")) {
            return new Kunde(
                    id,
                    benutzerErkennung,
                    benutzerVorNachName,
                    benutzerPasswort
            );
        } else {
            return new Mitarbeiter(
                    id,
                    benutzerErkennung,
                    benutzerVorNachName,
                    benutzerPasswort
            );
        }
    }
}