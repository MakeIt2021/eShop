package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn eine benötigte Datei
 * nicht gefunden oder nicht geöffnet werden kann.
 *
 * Sie kann sowohl den Dateinamen als auch die ursprüngliche
 * Ursache des Fehlers enthalten.
 *
 */
public class DateiNichtGefundenException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception für eine nicht gefundene Datei.
     *
     * @param e Name oder Pfad der nicht gefundenen Datei
     */
    public DateiNichtGefundenException(String e) {
        super("Diese Datei konnte nicht gefunden werden: " + e);
    }

    /**
     * Erzeugt eine neue Exception für eine nicht gefundene Datei
     * und speichert zusätzlich die ursprüngliche Ursache.
     *
     * @param e Name oder Pfad der nicht gefundenen Datei
     * @param t ursprüngliche Ursache des Fehlers
     */
    public DateiNichtGefundenException(String e, Throwable t) {
        super("Diese Datei konnte nicht gefunden werden: "
                + e
                + "\nDie Ursache ist: "
                + t);
    }
}