package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn bei einem
 * Massengutartikel eine Menge gewählt wird, die
 * nicht durch die Packungsgröße teilbar ist.
 *
 * Massengutartikel dürfen nur in Vielfachen ihrer
 * Packungsgröße bestellt oder verkauft werden.
 */
public class MassengutartikelmengeNichtTeilbarException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception, wenn die gewünschte
     * Menge nicht durch die Packungsgröße teilbar ist.
     *
     * @param bezeichnung    Bezeichnung des Massengutartikels
     * @param packungGroesse Packungsgröße des Artikels
     */
    public MassengutartikelmengeNichtTeilbarException(
            String bezeichnung,
            int packungGroesse) {

        super("Artikel mit Bezeichnung "
                + bezeichnung
                + " ist ein Massengutartikel! "
                + "Deshalb muss die Menge durch "
                + packungGroesse
                + " teilbar sein!");
    }
}