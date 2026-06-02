package entities;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public record GekaufterArtikel(
            int packungGroesse,
            String bezeichnung,
            double preis,
            double summe,
            int menge
    ) {}

    public ArrayList<GekaufterArtikel> gibAlleGekaufteArtikel() {
        ArrayList<GekaufterArtikel> alleGekaufteArtikel = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : warenkorbListe.entrySet()) {
            Artikel curArt = artikelListe.get(entry.getKey());

            int packungGroesse, menge;
            String bezeichnung = curArt.getBezeichnung();
            if (curArt instanceof Massengutartikel) {
                packungGroesse = ((Massengutartikel) curArt).getPackungGroesse();
                bezeichnung += " [" + ((Massengutartikel) curArt).getPackungGroesse() + "]";
                menge = entry.getValue() / ((Massengutartikel) curArt).getPackungGroesse();
            } else {
                packungGroesse = 1;
                menge = entry.getValue();
            }

            double preis = curArt.getPreis();
            double summe = preis * menge;

            alleGekaufteArtikel.add(new GekaufterArtikel(packungGroesse, bezeichnung, preis, summe, menge));
        }

        return alleGekaufteArtikel;
    }

    public double getSumme() {
        double summe = 0;
        for (GekaufterArtikel artikel : gibAlleGekaufteArtikel()) {
            summe += artikel.summe();
        }

        return summe;
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
