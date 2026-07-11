package eShop.common.entities;

import java.math.BigDecimal;

public class Massengutartikel extends Artikel {
    int artikelID;
    String bezeichnung;
    BigDecimal preis;
    private int packungGroesse;

    public Massengutartikel(int artikelID, String bezeichnung, BigDecimal preis, int packungGroesse) {
        super(artikelID, bezeichnung, preis);
        this.packungGroesse = packungGroesse;
    }

    public int getPackungGroesse() {
        return packungGroesse;
    }

    @Override
    public String toString() {
        return super.toString() + " Packungsgröße: " + packungGroesse + " |";
    }

    public void setPackungGroesse(int neueGroesse) {
        packungGroesse = neueGroesse;
    }

    @Override
    public String toNetworkString() {
        return getArtikelID() + ";" + getBezeichnung() + ";" + getPreis() + ";" + packungGroesse;
    }
}
