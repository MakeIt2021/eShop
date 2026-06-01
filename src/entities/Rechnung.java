package entities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Rechnung {
    private String kundeName;
    private HashMap<Integer, Integer> warenkorbListe;
    private HashMap<Integer, Artikel> artikelListe;
    private static final double MWST = 0.19;

    public Rechnung(String kundeName, HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        this.kundeName = kundeName;
        this.warenkorbListe = warenkorbListe;
        this.artikelListe = artikelListe;
    }

    public double getSumme() {
        double summe = 0;
        for (Map.Entry<Integer, Integer> entry : warenkorbListe.entrySet()) {
            Artikel curArt = artikelListe.get(entry.getKey());
            if (!(curArt instanceof Massengutartikel)) {
                summe += entry.getValue() * curArt.getPreis();
            } else {
                summe += entry.getValue() * curArt.getPreis() / ((Massengutartikel) curArt).getPackungGroesse();
            }

        }

        return summe;
    }

    public HashMap<Integer, Integer> gibWarenkorbListe() {
        return warenkorbListe;
    }

    public double getMwst()   {
        return MWST * getSumme();
    }

    public double getGesamtPreis() {
        return getMwst() + getSumme();
    }

    public String getKundeName() {
        return kundeName;
    }

    public String getHeutigesDatum() {
        return String.valueOf(LocalDate.now());
    }
}
