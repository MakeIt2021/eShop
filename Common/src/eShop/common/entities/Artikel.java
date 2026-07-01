package eShop.common.entities;

import java.math.BigDecimal;

public class Artikel {
    private int artikelID;
    private String bezeichnung;
    private BigDecimal preis;

    public Artikel(int artikelID, String bezeichnung, BigDecimal preis) {
        this.artikelID = artikelID;
        this.bezeichnung = bezeichnung;
        this.preis = preis;
    }

    public String toString() {
        return ("Artikel ID: " + artikelID + " | Bezeichnung: " + bezeichnung + " | Preis: " + preis + " |");
    }

    public int getArtikelID() {
        return artikelID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public BigDecimal getPreis() {
        return preis;
    }

    public void setBezeichnung(String newBezeichnung) {
        this.bezeichnung = newBezeichnung;
    }

    public void setPreis(BigDecimal newPreis) {
        this.preis = newPreis;
    }
}