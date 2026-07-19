package eShop.common.exceptions;

import java.math.BigDecimal;

/**
 * Diese Exception wird ausgelöst, wenn ein ungültiger
 * Artikelpreis angegeben wird.
 *
 * Ein gültiger Preis darf nicht negativ sein.
 *
 */
public class UngueltigerPreisException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception für einen ungültigen Preis.
     *
     * @param preis der ungültige Artikelpreis
     */
    public UngueltigerPreisException(BigDecimal preis) {
        super("Ungültiger Preis: "
                + preis
                + ". Der Preis darf nicht negativ sein.");
    }
}
