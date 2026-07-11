package eShop.common.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public String toNetworkString() {
        return artikelID + ";" + bezeichnung + ";" + preis + ";" + "0";
    }

    public static Artikel fromNetworkString(String line) {
        if (line == null || line.equals("null"))
            return null;

        String[] parts = line.split(";");
        int id = Integer.parseInt(parts[0]);
        String bezeichnung = parts[1];
        BigDecimal preis = new BigDecimal(parts[2]).setScale(2, RoundingMode.HALF_EVEN);
        int packungsGroesse = Integer.parseInt(parts[3]);

        if (packungsGroesse == 0) {
            return new Artikel(id, bezeichnung, preis);
        } else {
            return new Massengutartikel(id, bezeichnung, preis, packungsGroesse);
        }
    }
}