package eShop.common.entities;

/**
 * Repräsentiert einen Mitarbeiter des eShops.
 *
 * Die Klasse erweitert die Klasse {@code Benutzer}
 * und beschreibt einen Benutzer mit der Rolle
 * "Mitarbeiter".
 */
public class Mitarbeiter extends Benutzer {
    /**
     * Erstellt einen neuen Mitarbeiter.
     *
     * @param benutzerId eindeutige ID des Mitarbeiters
     * @param benutzerErkennung Benutzerkennung des Mitarbeiters
     * @param benutzerVorNachname Vor- und Nachname des Mitarbeiters
     * @param benutzerPassword Passwort des Mitarbeiters
     */
    public Mitarbeiter(
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
     * @return die Rolle "Mitarbeiter"
     */
    @Override
    public String getRole() {
        return "Mitarbeiter";
    }
}