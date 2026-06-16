package entities;

public class Artikel {
    private int artikelID;
    private String bezeichnung;
    private float preis;

    public Artikel(int artikelID, String bezeichnung, float preis) {
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

    public float getPreis() {
        return preis;
    }

    public void setBezeichnung(String newBezeichnung) {
        this.bezeichnung = newBezeichnung;
    }

    public void setPreis(float newPreis) {
        this.preis = newPreis;
    }
}