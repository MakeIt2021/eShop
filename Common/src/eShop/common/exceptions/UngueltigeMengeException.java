package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn eine ungültige
 * Artikelmenge angegeben wird.
 *
 * Eine gültige Menge muss größer als 0 sein.
 *
 */
public class UngueltigeMengeException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception für eine ungültige Artikelmenge.
     *
     * @param menge die ungültige Menge
     */
    public UngueltigeMengeException(int menge) {
        super("Ungültige Menge: " + menge
                + ". Die Menge muss größer als 0 sein.");
    }
}