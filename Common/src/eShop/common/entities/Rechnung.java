package eShop.common.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert eine Rechnung für einen abgeschlossenen Kauf im EShop.
 *
 * Diese Klasse berechnet die Einzelpositionen, die Netto-Gesamtsumme,
 * die Mehrwertsteuer (MwSt.) sowie den Brutto-Endpreis basierend
 * auf dem übergebenen Warenkorb und den aktuellen Artikeldaten.
 *
 * @author Bulat Valiullin
 * @version 1.0
 */

public class Rechnung {
    private String kundeName;
    private HashMap<Integer, Integer> warenkorbListe;
    private HashMap<Integer, Artikel> artikelListe;
    private static final BigDecimal MWST = new BigDecimal("0.19");

    /**
     * Erstellt ein neues Rechnungsobjekt für einen Kunden.
     *
     * @param kundeName       der Name des Kunden, für den die Rechnung ausgestellt wird
     * @param warenkorbListe  die Map der gekauften Artikel (Schlüssel: ArtikelID, Wert: Gesamtstückzahl)
     * @param artikelListe    die Map aller im System registrierten Artikel zur Preis- und Bezeichnungsermittlung
     */
    public Rechnung(String kundeName, HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        this.kundeName = kundeName;
        this.warenkorbListe = warenkorbListe;
        this.artikelListe = artikelListe;
    }

    /**
     * Ein Record, der eine einzelne berechnete Position auf der Rechnung darstellt.
     *
     * @param packungGroesse die Stückzahl pro Packung (1 bei Standardartikeln)
     * @param bezeichnung    der Name des Artikels (ggf. mit Packungsinformation erweitert)
     * @param preis          der Netto-Preis pro Verkaufseinheit (Einzelpreis oder Packungspreis)
     * @param summe          die Netto-Gesamtsumme dieser Position (Menge * Preis)
     * @param menge          die Anzahl der gekauften Verkaufseinheiten (Stück oder Packungen)
     */
    public record GekaufterArtikel(
            int packungGroesse,
            String bezeichnung,
            BigDecimal preis,
            BigDecimal summe,
            int menge
    ) {}

    /**
     * Erzeugt eine Liste aller gekauften Artikel.
     *
     * Für Massengutartikel wird zusätzlich die
     * Packungsgröße berücksichtigt.
     *
     * @return Liste aller gekauften Artikel
     */
    public ArrayList<GekaufterArtikel> gibAlleGekaufteArtikel() {

        ArrayList<GekaufterArtikel> alleGekaufteArtikel =
                new ArrayList<>();

        // Alle Warenkorbartikel durchlaufen.
        for (Map.Entry<Integer, Integer> entry : warenkorbListe.entrySet()) {

            Artikel curArt =
                    artikelListe.get(entry.getKey());

            int packungGroesse;
            int menge;

            String bezeichnung =
                    curArt.getBezeichnung();

            // Massengutartikel gesondert behandeln.
            if (curArt instanceof Massengutartikel) {

                packungGroesse =
                        ((Massengutartikel) curArt)
                                .getPackungGroesse();

                bezeichnung +=
                        " ("
                                + packungGroesse
                                + " in der Packung)";

                menge =
                        entry.getValue()
                                / packungGroesse;

            } else {

                packungGroesse = 1;
                menge = entry.getValue();
            }

            BigDecimal preis =
                    curArt.getPreis();

            BigDecimal summe =
                    preis.multiply(
                                    BigDecimal.valueOf(menge)
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_EVEN
                            );

            alleGekaufteArtikel.add(
                    new GekaufterArtikel(
                            packungGroesse,
                            bezeichnung,
                            preis,
                            summe,
                            menge
                    )
            );
        }

        return alleGekaufteArtikel;
    }

    /**
     * Berechnet die Zwischensumme aller
     * gekauften Artikel.
     *
     * @return Zwischensumme
     */
    public BigDecimal getSumme() {

        BigDecimal summe = BigDecimal.ZERO;

        for (GekaufterArtikel artikel : gibAlleGekaufteArtikel()) {

            summe = summe.add(
                    artikel.summe()
            );
        }

        return summe.setScale(
                2,
                RoundingMode.HALF_EVEN
        );
    }

    /**
     * Berechnet die Mehrwertsteuer
     * der Rechnung.
     *
     * @return Mehrwertsteuer
     */
    public BigDecimal getMwst() {

        return MWST.multiply(
                        getSumme()
                )
                .setScale(
                        2,
                        RoundingMode.HALF_EVEN
                );
    }

    /**
     * Berechnet den Gesamtpreis
     * inklusive Mehrwertsteuer.
     *
     * @return Gesamtpreis
     */
    public BigDecimal getGesamtPreis() {

        return getMwst().add(
                getSumme()
        );
    }

    /**
     * Gibt den Namen des Kunden zurück.
     *
     * @return Kundenname
     */
    public String getKundeName() {
        return kundeName;
    }

    /**
     * Gibt das aktuelle Datum zurück.
     *
     * Das Datum wird im ISO-Format
     * (YYYY-MM-DD) geliefert.
     *
     * @return heutiges Datum
     */
    public String getHeutigesDatum() {
        return String.valueOf(LocalDate.now());
    }
}