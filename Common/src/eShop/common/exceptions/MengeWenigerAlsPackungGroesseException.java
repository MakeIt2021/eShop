package eShop.common.exceptions;

/**
 * Diese Exception wird ausgelöst, wenn für einen
 * Massengutartikel eine Menge gewählt wird, die
 * kleiner als die vorgeschriebene Packungsgröße ist.
 *
 * Massengutartikel dürfen nur in Mengen bestellt oder
 * verkauft werden, die mindestens einer Packungsgröße
 * entsprechen.
 *
 */
public class MengeWenigerAlsPackungGroesseException extends RuntimeException {

    /**
     * Erzeugt eine neue Exception, wenn die gewünschte Menge
     * kleiner als die Packungsgröße ist.
     *
     * @param bezeichnung    Bezeichnung des Massengutartikels
     * @param menge          angeforderte Menge
     * @param packungGroesse vorgeschriebene Packungsgröße
     */
    public MengeWenigerAlsPackungGroesseException(
            String bezeichnung,
            int menge,
            int packungGroesse) {

        super(bezeichnung
                + " ist ein Massengutartikel. "
                + "Die Menge ("
                + menge
                + ") darf nicht kleiner als die Packungsgröße ("
                + packungGroesse
                + ") sein.");
    }
}