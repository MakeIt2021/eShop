package eShop.common.entities;

/*
 * Diese Klasse speichert jedes Lager-Ereignis.
 *
 * Ein Ereignis bedeutet:
 * - Einlagerung
 * - Auslagerung
 *
 * Gespeichert werden:
 * - Datum (Tag des Jahres)
 * - Artikel
 * - Menge
 * - beteiligte Person
 * - Typ des Ereignisses
 */

import java.time.LocalDate;

public class Ereignis {
    private LocalDate tag;
    private Artikel artikel;
    private int menge;
    private String typ; // EINLAGERUNG oder AUSLAGERUNG
    private String person;

    public Ereignis(LocalDate tag, Artikel artikel, int menge, String typ, String person) {
        this.tag = tag;
        this.artikel = artikel;
        this.menge = menge;
        this.typ = typ;
        this.person = person;
    }

    public LocalDate getTag() {
        return tag;
    }

    public Artikel getArtikel() {
        return artikel;
    }

    public int getMenge() {
        return menge;
    }

    public String getTyp() {
        return typ;
    }

    public String getPerson() {
        return person;
    }

    public String toString() {
        int id = (this.artikel != null) ? this.artikel.getArtikelID() : -1;
        String name = (this.artikel != null) ? this.artikel.getBezeichnung() : "Entfernter Artikel";

        return "Tag: " + tag + " | Typ: " + typ
                + " | ArtikelID: " + id
                + " | Artikel: " + name
                + " | Menge: " + menge
                + " | Person: " + person;
    }

    public String toNetworkString() {
        return tag + ";" + artikel.toNetworkString() + ";" + menge + ";" + typ + ";" + person;
    }

    public static Ereignis fromNetworkString(String line) {
        if (line == null || line.equals("null"))
            return null;

        String[] parts = line.split(";");
        LocalDate tag = LocalDate.parse(parts[0]);
        Artikel a = Artikel.fromNetworkString(parts[1]);
        int menge = Integer.parseInt(parts[2]);
        String typ = parts[3];
        String person = parts[4];

        return new Ereignis(tag, a, menge, typ, person);
    }
}