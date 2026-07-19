package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn ein gesuchter Artikel
 * im eShop nicht gefunden werden kann.
 *
 * Ein Artikel kann entweder über seine Artikel-ID oder über
 * seine Bezeichnung gesucht werden.

 */
public class ArtikelExistiertNichtException extends RuntimeException {
    /**
     * Erzeugt eine neue Exception, wenn kein Artikel mit der
     * angegebenen Bezeichnung existiert.
     *
     * @param bezeichnung die Bezeichnung des nicht gefundenen Artikels
     */
    public ArtikelExistiertNichtException(String bezeichnung) {
        super("Artikel mit Bezeichnung \""
                + bezeichnung
                + "\" existiert nicht.");
    }
}