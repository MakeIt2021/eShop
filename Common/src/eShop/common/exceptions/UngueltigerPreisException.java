package eShop.common.exceptions;

import java.math.BigDecimal;

public class UngueltigerPreisException extends RuntimeException {

    public UngueltigerPreisException(BigDecimal preis) {
        super("Ungültiger Preis: " + preis + ". Der Preis darf nicht negativ sein.");
    }
}
