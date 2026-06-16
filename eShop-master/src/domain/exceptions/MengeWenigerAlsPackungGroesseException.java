package domain.exceptions;

public class MengeWenigerAlsPackungGroesseException extends RuntimeException {
    public MengeWenigerAlsPackungGroesseException(String bezeichnung, int menge, int packungGroesse) {
        super(bezeichnung + " ist ein Massengutartikel. Die menge (" + menge + ") darf nicht weniger als packungGroesse (" + packungGroesse + ") sein");
    }
}
