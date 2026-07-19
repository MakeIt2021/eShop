package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn die angeforderte
 * Artikelmenge den verfügbaren Bestand überschreitet.
 *
 * Sie informiert darüber, welcher Artikel betroffen ist,
 * wie viele Exemplare verfügbar sind und welche Menge
 * angefordert wurde.
 *

 */
public class BestandNichtAusreichendException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception, wenn der Lagerbestand
     * für den gewünschten Artikel nicht ausreicht.
     *
     * @param bezeichnung        Bezeichnung des Artikels
     * @param bestand            aktuell verfügbarer Bestand
     * @param angeforderteMenge  vom Kunden gewünschte Menge
     */
    public BestandNichtAusreichendException(
            String bezeichnung,
            int bestand,
            int angeforderteMenge) {

        super("Artikel \"" + bezeichnung
                + "\": Bestand = "
                + bestand
                + ", angefordert = "
                + angeforderteMenge
                + ". Nicht genug Bestand vorhanden.");
    }
}