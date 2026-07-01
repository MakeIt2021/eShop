package eShop.common.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rechnung {
    private String kundeName;
    private HashMap<Integer, Integer> warenkorbListe;
    private HashMap<Integer, Artikel> artikelListe;
    private static final BigDecimal MWST = new BigDecimal("0.19");

    public Rechnung(String kundeName, HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        this.kundeName = kundeName;
        this.warenkorbListe = warenkorbListe;
        this.artikelListe = artikelListe;
    }

    public record GekaufterArtikel(
            int packungGroesse,
            String bezeichnung,
            BigDecimal preis,
            BigDecimal summe,
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
                bezeichnung += " (" + ((Massengutartikel) curArt).getPackungGroesse() + " in der Packung)";
                menge = entry.getValue() / ((Massengutartikel) curArt).getPackungGroesse();
            } else {
                packungGroesse = 1;
                menge = entry.getValue();
            }

            BigDecimal preis = curArt.getPreis();
            BigDecimal summe = preis.multiply(BigDecimal.valueOf(menge)).setScale(2, RoundingMode.HALF_EVEN);

            alleGekaufteArtikel.add(new GekaufterArtikel(packungGroesse, bezeichnung, preis, summe, menge));
        }

        return alleGekaufteArtikel;
    }

    public BigDecimal getSumme() {
        BigDecimal summe = BigDecimal.ZERO;
        for (GekaufterArtikel artikel : gibAlleGekaufteArtikel()) {
            summe = summe.add(artikel.summe());
        }

        return summe;
    }

    public BigDecimal getMwst()   {
        return MWST.multiply(getSumme()).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getGesamtPreis() {
        return getMwst().add(getSumme());
    }

    public String getKundeName() {
        return kundeName;
    }

    public String getHeutigesDatum() {
        return String.valueOf(LocalDate.now());
    }
}
