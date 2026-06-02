package domain.exceptions;

public class MassengutartikelmengeNichtTeilbarException extends RuntimeException {
    public MassengutartikelmengeNichtTeilbarException(String bezeichnung, int packungGroesse) {
        super("Artikel mit Bezeichnung " + bezeichnung
                + " ist ein Massengutartikel! Deshalb muss die Menge durch " + packungGroesse + " teilbar sein!");
    }
}
