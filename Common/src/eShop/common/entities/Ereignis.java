package eShop.common.entities;

import java.time.LocalDate;

/**
 * Repräsentiert ein Lagerereignis im eShop.
 *
 * Ein Ereignis dokumentiert eine Lagerbewegung eines Artikels,
 * beispielsweise eine Einlagerung oder eine Auslagerung.
 * Dabei werden das Datum, der betroffene Artikel, die Menge,
 * der Ereignistyp sowie die verantwortliche Person gespeichert.
 */

public class Ereignis {
    /**
     * Datum, an dem das Lagerereignis stattgefunden hat.
     */
    private LocalDate tag;

    /**
     * Artikel, auf den sich das Ereignis bezieht.
     */
    private Artikel artikel;

    /**
     * Anzahl der betroffenen Artikel.
     */
    private int menge;

    /**
     * Typ des Lagerereignisses
     * (z. B. EINLAGERUNG oder AUSLAGERUNG).
     */
    private String typ;

    /**
     * Person, die das Lagerereignis durchgeführt hat.
     */
    private String person;

    /**
     * Erstellt ein neues Lagerereignis.
     *
     * @param tag Datum des Ereignisses
     * @param artikel betroffener Artikel
     * @param menge Anzahl der Artikel
     * @param typ Typ des Ereignisses
     * @param person verantwortliche Person
     */
    public Ereignis(LocalDate tag, Artikel artikel, int menge, String typ, String person) {
        this.tag = tag;
        this.artikel = artikel;
        this.menge = menge;
        this.typ = typ;
        this.person = person;
    }

    /**
     * Gibt das Datum des Ereignisses zurück.
     *
     * @return Datum des Ereignisses
     */
    public LocalDate getTag() {
        return tag;
    }

    /**
     * Gibt den betroffenen Artikel zurück.
     *
     * @return Artikel des Ereignisses
     */
    public Artikel getArtikel() {
        return artikel;
    }

    /**
     * Gibt die betroffene Artikelmenge zurück.
     *
     * @return Artikelmenge
     */
    public int getMenge() {
        return menge;
    }

    /**
     * Gibt den Typ des Ereignisses zurück.
     *
     * @return Ereignistyp
     */
    public String getTyp() {
        return typ;
    }

    /**
     * Gibt die verantwortliche Person zurück.
     *
     * @return verantwortliche Person
     */
    public String getPerson() {
        return person;
    }

    /**
     * Erstellt eine lesbare Zeichenkette des Ereignisses.
     * Diese Darstellung eignet sich für die Ausgabe in der
     * Konsole oder in der Benutzeroberfläche.
     *
     * @return String-Darstellung des Ereignisses
     */
    @Override
    public String toString() {

        // Falls der Artikel gelöscht wurde, werden Standardwerte verwendet.
        int id = (this.artikel != null) ? this.artikel.getArtikelID() : -1;
        String name = (this.artikel != null)
                ? this.artikel.getBezeichnung()
                : "Entfernter Artikel";

        return "Tag: " + tag + " | Typ: " + typ
                + " | ArtikelID: " + id
                + " | Artikel: " + name
                + " | Menge: " + menge
                + " | Person: " + person;
    }

    /**
     * Wandelt das Ereignis in einen String um,
     * der über das Netzwerk übertragen werden kann.
     *
     * @return serialisierte Darstellung des Ereignisses
     */
    public String toNetworkString() {
        return tag + ";" + artikel.toNetworkString() + ";"
                + menge + ";" + typ + ";" + person;
    }

    /**
     * Erstellt aus einer serialisierten Netzwerkdarstellung
     * ein neues Ereignisobjekt.
     *
     * @param line serialisierte Darstellung eines Ereignisses
     * @return erzeugtes Ereignis oder {@code null},
     *         falls keine gültigen Daten vorhanden sind
     */
    public static Ereignis fromNetworkString(String line) {
        // Prüft, ob überhaupt Daten vorhanden sind.
        if (line == null || line.equals("null")) {
            return null;
        }

        // Zerlegt den Netzwerk-String in seine Bestandteile.
        String[] parts = line.split(";", 9);
        LocalDate tag = LocalDate.parse(parts[0]);
        Artikel artikel = Artikel.fromNetworkString(
                String.join(";", parts[1], parts[2], parts[3], parts[4]));

        int menge = Integer.parseInt(parts[5]);
        String typ = parts[6];
        String person = parts[7];

        // Erstellt und liefert das Ereignisobjekt zurück.
        return new Ereignis(tag, artikel, menge, typ, person);
    }
}