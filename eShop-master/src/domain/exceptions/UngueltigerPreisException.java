package domain.exceptions;

public class UngueltigerPreisException extends RuntimeException {

    public UngueltigerPreisException(float preis) {
        super("Ungültiger Preis: " + preis + ". Der Preis darf nicht negativ sein.");
    }
}
