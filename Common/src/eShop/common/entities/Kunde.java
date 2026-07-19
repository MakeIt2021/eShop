package eShop.common.entities;

/**
 * Repräsentiert einen Kunden des eShops.
 *
 * Die Klasse erweitert die Klasse {@code Benutzer}
 * und beschreibt einen Benutzer mit der Rolle "Kunde".
 */
public class Kunde extends Benutzer {

    /**
     * Erstellt einen neuen Kunden.
     *
     * @param benutzerId eindeutige ID des Kunden
     * @param benutzerErkennung Benutzerkennung des Kunden
     * @param benutzerVorNachname Vor- und Nachname des Kunden
     * @param benutzerPassword Passwort des Kunden
     */
    public Kunde(
            int benutzerId,
            String benutzerErkennung,
            String benutzerVorNachname,
            String benutzerPassword
    ) {
        super(
                benutzerId,
                benutzerErkennung,
                benutzerVorNachname,
                benutzerPassword
        );
    }

    /**
     * Gibt die Rolle des Benutzers zurück.
     *
     * @return die Rolle "Kunde"
     */
    @Override
    public String getRole() {
        return "Kunde";
    }
}