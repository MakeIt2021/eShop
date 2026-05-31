package domain.exceptions;

public class UngueltigeMengeException extends RuntimeException {

    public UngueltigeMengeException(int menge) {
        super("Ungültige Menge: " + menge + ". Die Menge muss größer als 0 sein.");
    }
}
