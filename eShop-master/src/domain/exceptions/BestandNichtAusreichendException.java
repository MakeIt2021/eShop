package domain.exceptions;

public class BestandNichtAusreichendException extends RuntimeException{

    public BestandNichtAusreichendException(String bezeichnung, int bestand, int angeforderteMenge) {
        super("Artikel \"" + bezeichnung + "\": Bestand = " + bestand + ", angefordert = " + angeforderteMenge + ". Nichr genug Bestand vorhanden.");
    }
}
