package entities;

public class Artikel {
    private int artikelID;
    private String bezeichnung;
    private int bestand;
    private int preis;

    public Artikel(int artikelNummer, String bezeichnung, int bestand, int preis) {
        this.artikelID = artikelNummer;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
    }

    public String toString() {
        return ("Artikel ID: " + artikelID + " / Bezeichnung: " + bezeichnung + " / " + "Bestand: " + bestand + " / Preis: " + preis + " /");
    }

    public int getArtikelID() {
        return artikelID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public int getBestand() {
        return bestand;
    }

    public int getpreis() {
        return preis;
    }

    public void bestandErhoehen() {
        this.bestand += 1;
    }

    public void bestandVerringern() {
        if (bestand > 0)
            this.bestand -= 1;
    }
}