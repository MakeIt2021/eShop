package eShop.common.entities;

import java.math.BigDecimal;

public class Massengutartikel extends Artikel {
    private int packungGroesse;

    public Massengutartikel(int artikelID, String bezeichnung, BigDecimal preis, int packungGroesse) {
        super(artikelID, bezeichnung, preis);
        this.packungGroesse = packungGroesse;
    }

    public int getPackungGroesse() {
        return packungGroesse;
    }

    public String toString() {
        return super.toString() + " Packungsgröße: " + packungGroesse + " |";
    }

    public void setPackungGroesse(int neueGroesse) {
        packungGroesse = neueGroesse;
    }

}
