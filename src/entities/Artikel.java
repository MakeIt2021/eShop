package entities;

public class Artikel {
    private int artikelID;
    private String bezeichnung;
    private int preis;

    public Artikel(int artikelNummer, String bezeichnung, int preis) {
        this.artikelID = artikelNummer;
        this.bezeichnung = bezeichnung;
        this.preis = preis;
    }

    public String toString() {
        return ("Artikel ID: " + artikelID + " / Bezeichnung: " + bezeichnung + " / Preis: " + preis + " /");
    }

    public int getArtikelID() {
        return artikelID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getPreis() {
        return preis;
    }
}