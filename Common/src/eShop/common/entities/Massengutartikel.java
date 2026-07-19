package eShop.common.entities;

import java.math.BigDecimal;

/**
 * Repräsentiert einen Massengutartikel im EShop, der von {@link Artikel} erbt.
 *
 * Ein Massengutartikel zeichnet sich dadurch aus, dass er im Gegensatz zu
 * Standardartikeln nur in festen Packungsgrößen
 * abgegeben und gelagert werden kann.
 *
 * Hinweis zur Preisgestaltung: Das geerbte Attribut {@code preis} repräsentiert bei
 * dieser Klasse den Netto-Verkaufspreis der **gesamten Packung** und nicht den
 * Preis eines einzelnen Artikels innerhalb der Packung.
 */

public class Massengutartikel extends Artikel {
    private int packungGroesse;

    public Massengutartikel(int artikelID, String bezeichnung, BigDecimal preis, int packungGroesse) {
        super(artikelID, bezeichnung, preis);
        this.packungGroesse = packungGroesse;
    }

    public int getPackungGroesse() {
        return packungGroesse;
    }

    /**
     * Der String erweitert die Darstellung der Basisklasse um die Packungsgröße.
     *
     * @return eine String-Repräsentation des Massengutartikels inklusive Packungsgröße
     */
    @Override
    public String toString() {
        return super.toString() + " Packungsgröße: " + packungGroesse + " |";
    }

    public void setPackungGroesse(int neueGroesse) {
        packungGroesse = neueGroesse;
    }

    /**
     * Konvertiert den Massengutartikel in ein bestimmtes Textformat für die Netzwerkübertragung.
     * Im Gegensatz zum Standardartikel steht am Ende die tatsächliche Größe der Packung statt der 0.
     *
     * @return der formatierte Netzwerk-String für die Übertragung zum Client/Server
     */
    @Override
    public String toNetworkString() {
        return getArtikelID() + ";" + getBezeichnung() + ";" + getPreis() + ";" + packungGroesse;
    }
}
